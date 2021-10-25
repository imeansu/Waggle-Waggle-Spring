package soma.test.waggle.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import soma.test.waggle.dto.photon.PhotonConversationDto;
import soma.test.waggle.dto.photon.PhotonMemberDto;
import soma.test.waggle.entity.*;
import soma.test.waggle.type.*;
import soma.test.waggle.repository.ConversationRepositoty;
import soma.test.waggle.repository.InterestRepository;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class InitDb {

    private final InitService initService;

    @PostConstruct
    public void init() {
        initService.securityContext("-1000");
        initService.basicInterestInsert();
        initService.dbInit1();
        initService.dbInit2();
    }

    @Transactional
    @Component
    @RequiredArgsConstructor
    static class InitService{

        private final EntityManager em;
        private final AuthService authService;
        private final ConversationRepositoty conversationRepositoty;
        private final WorldRoomService worldRoomService;
        private final InterestRepository interestRepository;

        private void securityContext(String id) {
            Collection<? extends GrantedAuthority> authorities =
                    Arrays.stream("ROLE_USER".split(","))
                            .map(SimpleGrantedAuthority::new)
                            .collect(Collectors.toList());
            UserDetails principal = new User(id, "", authorities);
            SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(principal, "", authorities));
        }

        public void dbInit1(){

            for (int i = 1; i <= 10; i++){
                WorldRoom worldRoom = new WorldRoom();
                worldRoom.setName("TEST:world_"+i);
                worldRoom.setOnStatus(i%2==0? OnStatusType.Y : OnStatusType.N);
                worldRoom.setKeywords(Arrays.asList("K-POP","enum말고","저놈!"));
                em.persist(worldRoom);
            }
            List<InterestMember> interestMembers = new ArrayList<>();
            Map<String, Interest> interestMap = interestRepository.findInterestMap(new ArrayList<>(Arrays.asList("K-POP", "스포츠", "축구", "BTS", "한국어", "IT")));
            List<Interest> interestList = new ArrayList<>(interestMap.values());
            List<CountryType> countryTypeList = Arrays.asList(CountryType.class.getEnumConstants());
            List<LanguageType> languageTypeList = Arrays.asList(LanguageType.class.getEnumConstants());
            for (int i = 0; i <= 10; i++){
                String j = Integer.toString(i);

                Member member = new Member("Test|email|"+j, "Test|name|"+j, i%2 == 0? AvatarType.MALE1 : AvatarType.FEMALE1,"Test|password|"+j, "Test|firebaseId|"+j, AuthorityType.ROLE_USER, LocalDate.now(), "Test|nickname|"+j
                        , i%2 == 0? OnStatusType.Y : OnStatusType.N, OnStatusType.N, "Test|introduction|"+j, interestMembers);
                member.setCountryType(countryTypeList.get(i%3));
                member.setLanguageType(languageTypeList.get(i%2));
                InterestMember interestMember1 = new InterestMember();
                InterestMember interestMember2 = new InterestMember();
                interestMember1.setInterest(interestList.get(i%6));
                interestMember1.setMember(member);
                em.persist(member);
                em.persist(interestMember1);
                interestMember2.setInterest(interestList.get((i+1)%6));
                interestMember2.setMember(member);
                em.persist(interestMember2);
            }
        }

        public void dbInit2(){
            WorldRoom worldRoom = createWorldRoom("TEST:Hi! Let's talk!");
            em.persist(worldRoom);

            Member member = createMember("TEST:minsu", "TEST:dgxc@vkdl.com");
            member.setNickname("TEST:imeansu");
            em.persist(member);

            worldRoomService.pathJoin(new PhotonMemberDto(worldRoom.getId(), member.getId()));

            PhotonConversationDto photonConversationDto1 = new PhotonConversationDto(worldRoom.getId(), "1234", member.getId(), "안녕 나는 민수야");

            worldRoomService.pathEvent(photonConversationDto1);
        }

        public void basicInterestInsert(){
            Interest root = Interest.builder()
                    .subject("root")
                    .build();
            interestRepository.save(root);
            List<String> interests = new ArrayList<>(Arrays.asList("K-POP", "스포츠", "축구", "BTS", "한국어", "IT"));
            interests.stream()
                    .forEach((string) -> interestRepository.save(Interest.builder()
                            .parent(root)
                            .subject(string)
                            .build()));

        }

        private WorldRoom createWorldRoom(String name) {
            WorldRoom worldRoom = new WorldRoom();
            worldRoom.setName(name);
            return worldRoom;
        }

        private Member createMember(String name, String email) {
            Member member1 = new Member();
            member1.setEmail(email);
            member1.setName(name);
            return member1;
        }


    }
}
