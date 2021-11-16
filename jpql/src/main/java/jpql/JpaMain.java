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

            for(int i = 0; i<100; i++) {
                Member member = new Member();
                member.setUsername("member" + i);
                member.setAge(i);

                Team team = new Team();
                team.setName("team" + i);
                em.persist(team);

                member.setTeam(team);
                em.persist(member);
            }

            // CASE
            String query = "select " +
                                "case when m.age <= 10 then '학생요금' " +
                                "     when m.age >= 60 then '경로요금' " +
                                "     else '일반요금' " +
                                "end " +
                            "from Member m";
            List<String> result = em.createQuery(query, String.class).getResultList();

            for (String s : result) {
                System.out.println("s = " + s);
            }

            // COALESCE
            Member member = new Member();
            member.setUsername(null);
            member.setAge(10);

            Team team = new Team();
            team.setName("teamA");
            em.persist(team);

            member.setTeam(team);
            em.persist(member);

            List<String> result2 = em.createQuery("select coalesce(m.username, '이름 없는 회원') from Member m", String.class).getResultList();

            for (String s : result2) {
                System.out.println("s = " + s);
            }

            // NULLIF
            Member member2 = new Member();
            member.setUsername("관리자");
            member.setAge(10);

            Team team2 = new Team();
            team.setName("teamA");
            em.persist(team);

            member.setTeam(team);
            em.persist(member);

            List<String> result3 = em.createQuery("select nullif(m.username, '관리자') from Member m", String.class).getResultList();

            for (String s : result3) {
                System.out.println("s = " + s);
            }

            tx.commit();
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

