package jpql;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.Objects;

public class JpaMain {

    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");

        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {

            for(int i=0; i<2; i++) {
                Team team = new Team();
                team.setName("team" + i);
                em.persist(team);

                Member member = new Member();
                member.setUsername("member" + i);
                member.setAge(i);
                member.setMemberType(MemberType.ADMIN);
                member.setTeam(team);

                member.changeTeam(team);


                em.persist(member);
            }

            em.flush();
            em.clear();

            // concat 합치기
            String query1 = "select concat('a', 'b') from Member m";
//            String s = "select 'a' || 'b' from Member m";

            // substring 자르기(2번째부터 4개)
            String query2 = "select substring(m.username, 2, 4) from Member m";

            // locate 찾기
            String query3 = "select locate('de', 'abcdef') from Member m";

            // size 반대 연관관계 사이즈 계산
            String query4 = "select size(t.members), t.name from Team t";
            List<Object[]> result = em.createQuery(query4).getResultList();

            for (Object[] objects : result) {
                System.out.println("objects[0] = " + objects[0]);
                System.out.println("objects[1] = " + objects[1]);
            }


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

