package jpql;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

public class JpaMain {

    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");

        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {

            for(int i=0; i<2; i++) {
                Member member = new Member();
                member.setUsername("member" + i);
                member.setAge(i);

                Team team = new Team();
                team.setName("team" + i);
                em.persist(team);

                member.changeTeam(team);
                member.setTeam(team);

                member.changeTeam(team);
                em.persist(member);
            }

            em.flush();
            em.clear();

            // 연관관계가 있을 시 (on t.name = 'team1' 조회 O)
            // inner 조인 (inner 생략 가능)
            List<Member> result = em.createQuery("select m from Member m inner join m.team t", Member.class).getResultList();

            // outer 조인 (left 생략 가능)
            List<Member> result2 = em.createQuery("select m from Member m left outer join m.team t ", Member.class).getResultList();

            // 연관관계가 없을 시
            // 막조인 - cross 조인 (세타 조인)
            List<Member> result3 = em.createQuery("select m from Member m, Team t where m.username = t.name", Member.class).getResultList();
            tx.commit();

            System.out.println("result3.size() = " + result3.size());

            // 막조인 - left 조인 (on member.TEAM_ID=team.id 조회 X)
           em.createQuery("select m from Member m left join Team t on m.username = t.name");


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

