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
import soma.test.waggle.entity.*;
import soma.test.waggle.repository.WorldRoomRepository;
import soma.test.waggle.type.*;
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
        initService.dbInit_worldRoom();
        initService.dbInit_member();
    }

    @Transactional
    @Component
    @RequiredArgsConstructor
    static class InitService{

        private final EntityManager em;
        private final InterestRepository interestRepository;
        private final WorldRoomRepository worldRoomRepository;
        private List<Member> members;
        List<WorldRoom> worldRooms;

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

        public void dbInit_member() {
            worldRooms = worldRoomRepository.findAllByCriteria(OnStatusType.Y);
            List<InterestMember> interestMembers = new ArrayList<>();
            Map<String, Interest> interestMap = interestRepository.findInterestMap(new ArrayList<>(Arrays.asList("K-POP", "스포츠", "축구", "BTS", "한국어", "IT")));
            List<Interest> interestList = new ArrayList<>(interestMap.values());
            List<CountryType> countryTypeList = Arrays.asList(CountryType.class.getEnumConstants());
            List<LanguageType> languageTypeList = Arrays.asList(LanguageType.class.getEnumConstants());
            members = new ArrayList<>(){{
                add(getMember("sam", AvatarType.MALE1, "samuel", OnStatusType.Y, "Hey everybody! I’m Interested in learning about everything Korean and Japanese. I am a native English speaker from the USA. I speak Spanish fluently as well. I was born and raised in Texas, which is part of the USA. I work in the medical field, edit videos, develop mobile apps, and DJ on the side. I’ll be glad to help you as best I can. I look forward to your message! Don’t be shy! I’ll make you laugh! I promise! \n" +
                        "\n" +
                        "Feel free to send me a message! ✍️ \n" +
                        "I like phone calls too! \uD83D\uDCDE \n" +
//                        "\n" +
                        "Sam"));
                add(getMember("gelila_lee", AvatarType.FEMALE1, "gelila_lee 리라", OnStatusType.Y, "ig-gelia_lee 친구하자 !! I just want to imporeve my Korean language skill"));
                add(getMember("Skyler Padilla", AvatarType.MALE2, "Skyler Padilla", OnStatusType.Y, "Helloo I would love to be friends and exchange languages."));
                add(getMember("test1", AvatarType.FEMALE3, "test1", OnStatusType.N, "test1"));
                add(getMember("조화영", AvatarType.FEMALE2, "조화영", OnStatusType.Y,"안녕하세요! 여기 만나서 반가워요^^. 저는 30살이고 지금 미국에 살고 있어요. 제가 이름 2개 있어요. 저의 미국이름 Heather 이고 한국이름 조화영라고 해요. 그래서 사용하기에 더 편한 것만 고르세요. 이름 2개 다 대답할게요^^. 한국어를 열심히 공부하지 않아서 요즘에 다시 열심히 한국어를 공부하고 싶어요."));
                add(getMember("Andrew", AvatarType.MALE3, "Andrew", OnStatusType.Y, "Hi everyone,\n" +
                        "\n" +
                        "Let's chat about daily life, our interests, and life experiences. I'm always happy to learn something new. \n" +
                        "\n" +
                        "My interests\n" +
                        "• cooking \uD83D\uDC68\u200D\uD83C\uDF73\n" +
                        "• photography \uD83D\uDCF7 \n" +
                        "• games \uD83C\uDFAE\n" +
                        "• nature \uD83C\uDFD5 \n" +
                        "\n" +
                        "Lately, I have become interested in Japanese movies from the 80's and 90's. If you have a recommendation, please let me know.\n" +
                        "\n" +
                        "I like to follow people with similar hobbies and interesting moments. If you've got some free time, come chat with me."));
                add(getMember("test2", AvatarType.FEMALE3, "test2", OnStatusType.N, "test2"));
                add(getMember("Lovisa 루비사", AvatarType.FEMALE4, "Lovisa 루비사", OnStatusType.Y, "한국에 살고 있어요.\n" +
                        "한국어를 오래 배웠지만 어려워서 아직 배울 게 많아요~\n" +
                        "\n" +
                        "채팅을 잘 안 해요..\n" +
                        "\n" +
                        "사진을 찍는 걸 좋아해요~ \n" +
                        "ig: @annalovisa.foto"));
                add(getMember("Tommy", AvatarType.MALE4, "Tommy", OnStatusType.Y, "    제 이름은 토마스지만 그냥 톰이라고 불러주세요. 저는 미국인이고 미주리에 살아요. 혹시 들어 본 적이 있어요? 미국의 중부에 있어요. 지역은 대부분 시골이에요. 현재 아이들이나 애완동물은 없어요. 저는 학생이고, 회사원입니다. 제 전공이 국제 연구예요. 직업은 집에서 자동차 부품을 팔아요. 좋은 직업이 있지만 지루해졌어요. 도전적인 것이 필요했어요.\n" +
                        "    그래서, 외국어를 배우고 싶었어요. 요즘 한국은 정말 인기있는 나라예요. 아시아 문화를 좋아해서 한국어를 배우기로했어요. 물론, 생각보다 너무 어려웠어요. 지금까지, 3년 정도 한국어를 공부했어요. 하지만, 가끔 바쁘거나 게을러요. 언젠가 유창하게 말 하고 싶어요. 코로나 바이러스 때문에, 한국에 아직 가본 적이 없어요.\n" +
                        "    저는 자유시간에는 운동하고 공부하고, 기계도 고치는 것을 좋아해요. 보통 일주일에 두세 번 산책해요. 또, 거의 하루 종일 음악을 들어요. 이 번달에는 일본어도 배우기 시작했어요. 지금까지는 히라가나만 배웠어요.\n" +
                        "    졸업한 후에 더 많은 시간 있으면 좋 겠어요. 세계를 변화시킬 수 있는 일을 하고 싶어요. 모든 사람들을 사랑하고 세상을 더 좋은 곳으로 만들고 싶어요.\n" +
                        "                       경청해 주셔서 감사합니다."));
                add(getMember("Joey", AvatarType.FEMALE3, "Joey", OnStatusType.Y, "Hi I'm Joey, I am from South Africa. \n" +
                        "My native language is English. I'm learning Korean. I can help you with English. \n" +
                        "\n" +
                        "Language and Culture exchange. \n" +
                        "Let's message or call. \n" +
                        "No perverts thanks."));

            }};
            for (int i = 0; i < 10; i++){
                Member member = members.get(i);
                member.setCountryType(countryTypeList.get(i%3));
                member.setLanguageType(languageTypeList.get(i%2));
                InterestMember interestMember1 = new InterestMember();
                InterestMember interestMember2 = new InterestMember();
                interestMember1.setInterest(interestList.get(i%6));
                interestMember1.setMember(member);
                em.persist(interestMember1);
                interestMember2.setInterest(interestList.get((i+1)%6));
                interestMember2.setMember(member);
                em.persist(interestMember2);
                em.persist(member);
                if (i%4 != 1) {
                    member.setEntranceStatus(OnStatusType.Y);
                    EntranceRoom entranceRoom = EntranceRoom.builder()
                            .member(members.get(i))
                            .worldRoom(worldRooms.get(i % 4))
                            .isLast(OnStatusType.Y)
                            .build();
                    em.persist(entranceRoom);
                }
                em.persist(member);
            }

        }

        private Member getMember(String name, AvatarType avatarType, String nickname, OnStatusType onStatusType, String introduction) {
            Member member = new Member("Test email", name, avatarType,"Test password", "Test firebaseId", AuthorityType.ROLE_USER, LocalDate.now(), nickname
                    , onStatusType, OnStatusType.N, introduction, new ArrayList<InterestMember>());
            return member;
        }

        public void dbInit_worldRoom(){

            worldRooms = new ArrayList<>(){{
                add(getWorldRoom("Let's talk in English", OnStatusType.Y, Arrays.asList("한국어", "영어", "언어교환", "free talking"), 3, WorldMapType.GWANGHWAMUN));
                add(getWorldRoom("Talk and Read '핸드폰'(Cell phones)", OnStatusType.Y, Arrays.asList("아이폰", "갤럭시", "KT 먹통 현상", "앱등이"), 5, WorldMapType.Jongmyo));
                add(getWorldRoom("주말에 뭐했어요?", OnStatusType.Y, Arrays.asList("주말", "weekend", "산책", "계룡산 스파게티"), 8, WorldMapType.GWANGHWAMUN));
                add(getWorldRoom("몇 가지 주제로 대화해요", OnStatusType.Y, Arrays.asList("오징어 게임", "달고나", "살인", "Netflix"), 2, WorldMapType.Jongmyo));
                add(getWorldRoom("test", OnStatusType.N, Arrays.asList("test1", "test2"), 3, WorldMapType.GWANGHWAMUN));

            }};
            for (int i = 0; i < 5; i++){
                WorldRoom worldRoom = worldRooms.get(i);
                em.persist(worldRoom);
            }

        }

        private WorldRoom getWorldRoom(String name, OnStatusType onStatusType, List<String> keywords, int people, WorldMapType map) {
            WorldRoom worldRoom = new WorldRoom();
            worldRoom.setName(name);
            worldRoom.setOnStatus(onStatusType);
            worldRoom.setKeywords(keywords);
            worldRoom.setPeople(people);
            worldRoom.setMap(map);
            return worldRoom;
        }

        private void securityContext(String id) {
            Collection<? extends GrantedAuthority> authorities =
                    Arrays.stream("ROLE_USER".split(","))
                            .map(SimpleGrantedAuthority::new)
                            .collect(Collectors.toList());
            UserDetails principal = new User(id, "", authorities);
            SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(principal, "", authorities));
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
