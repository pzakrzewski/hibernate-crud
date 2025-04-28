package pl.zakrzewski;

import org.hibernate.Session;
import org.hibernate.query.Query;
import pl.zakrzewski.entity.Drug;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class DrugDao {

    public void saveDrug(Drug drug) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        try {
            session.save(drug);
            session.getTransaction().commit();
        } catch (Exception e) {
            System.out.println("saveDrug - exception occurred!");
            session.getTransaction().rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    public Drug readDrug(Long id) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Drug drug = null;
        try {
            drug = session.get(Drug.class, id);
        } catch (Exception e) {
            System.out.println("readDrug - exception occurred!");
            e.printStackTrace();
        } finally {
            session.close();
        }
        return drug;
    }

    public void deleteDrug(Long id) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        try {
            Drug drug = session.find(Drug.class, id);
            session.delete(drug);
            session.getTransaction().commit();
        } catch (Exception e) {
            System.out.println("deleteDrug - exception occurred!");
            session.getTransaction().rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    public void updateDrug(Drug newDrug) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        try {
            Drug drug4db = session.find(Drug.class, newDrug.getId());
            drug4db.setDrugType(newDrug.getDrugType());
            drug4db.setProdYear(newDrug.getProdYear());
            drug4db.setPrice(newDrug.getPrice());
            drug4db.setName(newDrug.getName());
            session.getTransaction().commit();
        } catch (Exception e) {
            System.out.println("deleteDrug - exception occurred!");
            session.getTransaction().rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    public List<Drug> getDrugsByPower(DrugType drugType) {
        List<Drug> drugs = new ArrayList<>();
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        try {
            Query query = session.createNativeQuery("SELECT id FROM drugs WHERE drugType LIKE ?");
            query.setParameter(1, drugType.toString());
            List<BigInteger> resultList = query.getResultList();
            for (BigInteger id : resultList) {
                drugs.add(readDrug(id.longValue()));
            }
        } catch (Exception e) {
            System.out.println("getDrugsByPower - exception occurred!");
            session.getTransaction().rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
        return drugs;
    }

}