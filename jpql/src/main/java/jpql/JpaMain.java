package jpql;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class JpaMain {

    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");

        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {

            Team teamA = new Team();
            teamA.setName("팀A");
            em.persist(teamA);

            Team teamB = new Team();
            teamB.setName("팀B");
            em.persist(teamB);

            Member member1 = new Member();
            member1.setUsername("회원1");
            member1.setTeam(teamA);
            em.persist(member1);

            Member member2 = new Member();
            member2.setUsername("회원2");
            member2.setTeam(teamA);
            em.persist(member2);

            Member member3 = new Member();
            member3.setUsername("회원3");
            member3.setTeam(teamB);
            em.persist(member3);

            em.flush();
            em.clear();

            // 일반 쿼리문이나 연관관계로 다른 엔티티를 불러 오는 경우
            List<Member> result0 = em.createQuery("select m from Member m", Member.class).getResultList();
            for (Member member : result0) {
                System.out.println("member.getUsername() = " + member.getUsername() + ", " + member.getTeam().getName());
                // 팀A의 이름을 가져올 때 프록시(실행된 적 없어서)이므로 영속성 컨텍스트에 있는지 확인한다. -> 커리(SQL) 실행
                // 그제서야 영속성 컨텍스트에 팀A가 들어간다.
                // 회원1, 팀A(SQL)
                // 회원2, 팀A(1차 캐시)
                // 회원3, 팀B(SQL) -> 영속성 컨텍스트에 없으므로 커리를 날려 영속성 컨텍스트에 올리고 결과를 반환한다.

                // 회원이 100명이면 1(회원 전체 조회 쿼리) + N(팀마다) 쿼리가 발생할 수 있다. -> fetch 조인으로 해결
                // getTeam은 프록시다. 조인 때 멤버만 가져왔기 때문이다.
            }

            em.flush();
            em.clear();

            // 페치 조인(쿼리 한 방에 팀을 전부 불러온다.)
            String query1 = "select m from Member m join fetch m.team"; // 명시적 조인을 써야 쿼리 튜닝이 쉽다. 묵시적 조인 시 collection으로 접근하기 때문에 field에 접근할 수 없다.(members.username 등)
            // join fetch 시 한방쿼리가 나간다. LAZY여도 페치 조인이 먼저 적용된다.
            List<Member> result1 = em.createQuery(query1, Member.class).getResultList();
            for (Member member : result1) {
                System.out.println("member.getUsername() = " + member.getUsername() + ", " + member.getTeam().getName());
                // getTeam은 프록시가 아니다. 조인 때 같이 가져왔기 때문이다.
            }
            // 2개 출력
            System.out.println("result1.size() = " + result1.size());

            // 페치 조인은 객체 그래프를 SQL 한 번에 조회하는 개념이다.
            // 페치 조인을 사용할 때만 연관된 엔티티도 함께 조회한다.(즉시 로딩)

            em.flush();
            em.clear();

            // 페치 조인에 별칭을 줄 수 없다. (JPA 설계상 모두 가져오기를 바란다.)
            // 페치 조인은 관련된 모든 것을 끌고 온다.(걸러서 가져오고 싶으면 페치 조인을 쓰면 안된다.)
            String query2 = "select t from Team t join fetch t.members";
            String query2_1 = "select t from Team t join fetch t.members as m";
            List<Team> result2 = em.createQuery(query2, Team.class).getResultList();
            for (Team team : result2) {
                System.out.println("team.getName() = " + team.getName() + "|members = " + team.getMembers().size());
                // team.getName() = 팀A|members = 2
                // team.getName() = 팀A|members = 2 // 조인 시 팀A에 멤버가 2명이므로 2개가 생성 된다. -> 2줄 생성 된다. (중복)
                // 영속성 컨텍스트에 이미 올라가서 메모리는 하나만 잡아먹지만 결과는 2개다.
                // team.getName() = 팀B|members = 1
                for (Member member : team.getMembers()) {
                    System.out.println("-> member = " + member);
                    System.out.println("member.getClass() = " + member.getClass());
                }
            }
            // 3개 출력 (조인하면서 늘어났다.)
            System.out.println("result2.size() = " + result2.size());

            em.flush();
            em.clear();

            // 컬렉션 페치 조인시에는 distinct를 적용해야 중복이 없다.(DB 차원이 아닌 JPA 차원에서 제거해준다.)
            String query3 = "select distinct t from Team t join fetch t.members";
            // SQL의 distinct는 팀A 회원1, 팀A 회원2라 distinct여도 중복 제거가 안된다. / JPQL의 distinct는 가능하다.
            List<Team> result3 = em.createQuery(query3, Team.class).getResultList();
            // 2개 출력
            System.out.println("result3.size() = " + result3.size());

            em.flush();
            em.clear();

            // 일반 조인 시 select문에서 1개 가져올 때
            String query4 = "select m from Member m join m.team";
            List<Member> result4 = em.createQuery(query4, Member.class).getResultList();

            for (Member member : result4) {
                System.out.println("member.getTeam().getClass() = " + member.getTeam().getClass());
                // getTeam은 프록시다. 조인 때 멤버만 가져왔기 때문이다.
                // select 절에 지정한 엔티티만 조회한다.
                // 객체 한 개로 가져온다.(결과 한 개)
            }

            em.flush();
            em.clear();

            //  일반 조 인시 select문에서 다 가져올 때
            String query5 = "select m,t From Member m join m.team t";
            List<Object[]> result5= em.createQuery(query5).getResultList();

            for (Object[] o : result5) {
                System.out.println(o);
                // 객체 두 개로 가져온다.(결과 두 개)
            }

            em.flush();
            em.clear();

            // 컬랙션 페치 조인은 페이징이 안된다. 아래처럼 해결한다.(+@BatchSize)
            // @BatchSize를 사용하면 in 쿼리로 where 절을 여러개 받을 수 있다.
            String query6 = "select t From Team t";
            List<Team> result6 = em.createQuery(query6, Team.class)
                    .setFirstResult(0)
                    .setMaxResults(2)
                    .getResultList();

            System.out.println("result6.size() = " + result6.size());

            for (Team team : result6) {
                System.out.println("team.getName() = " + team.getName() + "|members" + team.getMembers());
                for (Member member : team.getMembers()) {
                    System.out.println("-> member = " + member);

                }
            }

            em.flush();
            em.clear();

            // 엔티티 직접 사용
            // 엔티티를 파라미터로 전달
            String jpql1 = "select m from Member m where m = :member";
            List resultList1 = em.createQuery(jpql1)
                    .setParameter("member", member1)
                    .getResultList();

            String jpql2 = "select m from Member m where m.id = :memberId";
            List resultList2 = em.createQuery(jpql2)
                    .setParameter("memberId", member1.getId())
                    .getResultList();

            em.flush();
            em.clear();

            List<Member> result7 = em.createNamedQuery("Member.findByUsername", Member.class)
                    .setParameter("username", "회원1")
                    .getResultList();

            for (Member member : result7) {
                System.out.println("member = " + member);
            }

            em.flush();
            em.clear();

            // 벌크 연산
            int resultCount = em.createQuery("update Member m set m.age = 20").executeUpdate();

            System.out.println("resultCount = " + resultCount);
            em.clear();

            Member findMember = em.find(Member.class, member1.getId());
            System.out.println("findMember.getAge() = " + findMember.getAge());

        }
       catch (Exception e) {
            tx.rollback();
            e.printStackTrace();
        }
        finally {
            em.close();
        }

        emf.close();
    }
}

