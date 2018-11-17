package org.mw.mongodb.data;

import com.mongodb.MongoClient;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import org.mongodb.morphia.query.UpdateResults;
import org.mw.mongodb.data.model.Employee;

import java.util.List;

public class MorphiaMain {

    // http://morphiaorg.github.io/morphia/1.4/getting-started/

    public static void main(String[] args) {
        final Morphia morphia = new Morphia();
        morphia.mapPackage("org.mw.mongodb.data.model");

        final Datastore datastore = morphia.createDatastore(new MongoClient(), "morphia_example");
        datastore.ensureIndexes();

        final Employee elmer = new Employee("Elmer Fudd", 50000.0);
        final Employee daffy = new Employee("Daffy Duck", 40000.0);
        final Employee pepe = new Employee("Pepé Le Pew", 25000.0);

        datastore.save(elmer);
        datastore.save(daffy);
        datastore.save(pepe);

        elmer.getDirectReports().add(daffy);
        elmer.getDirectReports().add(pepe);

        datastore.save(elmer);

        // ---

        final Query<Employee> query = datastore.createQuery(Employee.class);
        final List<Employee> employees = query.asList();
        System.out.println(employees);

        List<Employee> underpaid1 = datastore.createQuery(Employee.class)
                .field("salary").lessThanOrEq(30000)
                .asList();

        List<Employee> underpaid2 = datastore.createQuery(Employee.class)
                .filter("salary <=", 30000)
                .asList();

        System.out.println(underpaid1);
        System.out.println(underpaid2);

        // ---

        final Query<Employee> underPaidQuery = datastore.createQuery(Employee.class)
                .filter("salary <=", 30000);

        final UpdateOperations<Employee> updateOperations = datastore.createUpdateOperations(Employee.class)
                .inc("salary", 10000);

        final UpdateResults results = datastore.update(underPaidQuery, updateOperations);

        System.out.println(results);

        // ---

        final Query<Employee> overPaidQuery = datastore.createQuery(Employee.class)
                .filter("salary >", 100000);

        datastore.delete(overPaidQuery);
    }
}
