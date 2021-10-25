package soma.test.waggle.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import soma.test.waggle.dto.MemberInfoDto;
import soma.test.waggle.entity.*;
import soma.test.waggle.type.OnStatusType;
import soma.test.waggle.util.SecurityUtil;

import javax.persistence.EntityManager;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MemberRepository {

    private final EntityManager em;
    private final RefreshTokenRepository refreshTokenRepository;

    public Member save(Member member){
        em.persist(member);
        return member;
    }

    public Optional<Member> findById(Long id){
        return Optional.ofNullable(em.find(Member.class, id));

    }

    public Member find(Long id){
        return  em.find(Member.class, id);
    }

    public Optional<Member> findByEmail(String email){
        return Optional.ofNullable(em.createQuery("select m from Member m where m.email = :email", Member.class)
                .setParameter("email", email)
                .getSingleResult());
    }

    public List<Member> findByName(String name){
        return em.createQuery(
                "select m from Member m" +
                        " where m.name = :name", Member.class)
                .setParameter("name", name)
                .getResultList();
    }

    public Long getMemberIdByFirebaseId(String id){
        String jpql = "select m from Member m where m.firebaseId = :firebaseId";
        List<Member> findMember = em.createQuery(jpql, Member.class)
                .setParameter("firebaseId", id)
                .getResultList();
        if (findMember.size() == 0){
            return -1L;
        }else{
            return findMember.get(0).getId();
        }
    }

    public List<Member> findAll(){
        return em.createQuery("select m from Member m", Member.class)
                .getResultList();
    }

    public List<Member> getOnlineFollowingMembers(){
        return getOnlineFollowingMembers(SecurityUtil.getCurrentMemberId());
    }
    public List<Member> getOnlineFollowingMembers(Long memberId) {
        Member member = em.find(Member.class, memberId);
//        System.out.println("SecurityUtil.getCurrentMemberId() = " + SecurityUtil.getCurrentMemberId());
        return em.createQuery("select m from Following f join f.followedMember m where f.followingMember = :member and m.onlineStatus = :status", Member.class)
                .setParameter("member", member)
                .setParameter("status", OnStatusType.Y)
                .getResultList();
    }

    public List<Member> getOnlineMembers(){
        return getOnlineMembers(SecurityUtil.getCurrentMemberId());
    }
    public List<Member> getOnlineMembers(Long memberId) {
        Member member = em.find(Member.class, memberId);
        return em.createQuery("select m from Member m where m.onlineStatus = :status", Member.class)
                .setParameter("status", OnStatusType.Y)
                .getResultList();
    }



//    public List<Member> findBlockMember(Long id){
//        Member member = em.find(Member.class, id);
//        return em.createQuery(
//                "select m from Blocking b" +
//                        " join b.blockedMember m" +
//                        " where b.blockingMember = :member", Member.class)
//                .setParameter("member", member)
//                .getResultList();
//    }

//    public List<Member> findBlockedByWho(Long id) {
//        Member member = em.find(Member.class, id);
//        return em.createQuery(
//                "select m from Blocking b" +
//                        " join b.blockedMember m" +
//                        " where m = :member", Member.class)
//                .setParameter("member", member)
//                .getResultList();
//    }

    public boolean deleteRefreshToken() {
        Optional<RefreshToken> refreshToken = refreshTokenRepository.findByKey(Long.toString(SecurityUtil.getCurrentMemberId()));
        if(refreshToken.isEmpty()){
            return false;
        } else{
            refreshTokenRepository.delete(refreshToken.get());
            return true;
        }
    }

    public boolean deleteMember() {
        Long memberId = SecurityUtil.getCurrentMemberId();
        try{
            em.remove(find(memberId));
            return true;
        } catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public List<Member> findByNickname(String nickname){
        return em.createQuery(
                "select m from Member m" +
                        " where m.nickname = :nickname", Member.class)
                .setParameter("nickname", nickname)
                .getResultList();
    }

    public boolean duplicationCheck(String nickname) {
        if (findByNickname(nickname).size() == 0){
            return true;
        } else {
            return false;
        }
    }



}
