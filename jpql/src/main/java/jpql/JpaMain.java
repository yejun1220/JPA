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

            String query1 = "select m from Member m join fetch m.team"; // 명시적 조인을 써야 쿼리 튜닝이 쉽다. 묵시적 조인 시 collection으로 접근하기 때문에 field에 접근할 수 없다.(members.username 등)
            // join fetch 시 한방쿼리가 나간다. LAZY여도 패치 조인이 먼저 먹힌다.
            List<Member> result1 = em.createQuery(query1, Member.class).getResultList();
            for (Member member : result1) {
                System.out.println("member.getUsername() = " + member.getUsername() + ", " + member.getTeam().getName());
                // 팀A의 이름을 가져올 때 프록시(실행된 적 없어서)이므로 영속성 컨텍스트에 있는지 확인한다. -> 커리(SQL) 실행
                // 그제서야 영속성 컨텍스트에 팀A가 들어간다.
                // 회원1, 팀A(SQL)
                // 회원2, 팀A(1차 캐시)
                // 회원3, 팀B(SQL) -> 영속성 컨텍스트에 없으므로 커리를 날려 영속성 컨텍스트에 올리고 결과를 반환한다.

                // 회원이 100명이면 1(회원 조회 쿼리) + N(팀마다) 쿼리가 발생할 수 있다.
            }
            // 2개 출력
            System.out.println("result1.size() = " + result1.size());

            String query2 = "select t from Team t join fetch t.members";
            List<Team> result2 = em.createQuery(query2, Team.class).getResultList();

            for (Team team : result2) {
                System.out.println("team.getName() = " + team.getName() + "|members = " + team.getMembers().size());
                // team.getName() = 팀A|members = 2
                // team.getName() = 팀A|members = 2 // 조인 시 팀A에 멤버가 2명이므로 2개가 생성 된다. -> 2줄 생성 된다. (중복)
                // 영속성 컨텍스트에 이미 올라가서 메모리는 하나만 잡아먹지만 결과는 2개다.
                // team.getName() = 팀B|members = 1
                for (Member member : team.getMembers()) {
                    System.out.println("-> member = " + member);
                }
            }
            // 3개 출력 (조인하면서 늘어났다.)
            System.out.println("result2.size() = " + result2.size());

            String query3 = "select distinct t from Team t join fetch t.members";
            // SQL의 distinct는 팀A 회원1, 팀A 회원2라 distinct여도 중복 제거가 안된다. / JPQL의 distinct는 가능하다.
            List<Team> result3 = em.createQuery(query3, Team.class).getResultList();
            // 2개 출력
            System.out.println("result3.size() = " + result3.size());
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

