package xyz.modshell.mongo.demo;

import com.mongodb.MongoClient;
import dev.morphia.Datastore;
import dev.morphia.Morphia;
import dev.morphia.query.Query;
import dev.morphia.query.UpdateOperations;
import dev.morphia.query.UpdateResults;
import org.junit.Assert;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;

/**
 * This class is used in the Quick Tour documentation and is used to demonstrate various Morphia features.
 */
public final class QuickTour {
//    @Value("${mongodb.host}")
//    private String host;
//
//    @Value("${mongodb.port}")
//    private Integer port;
//
//    @Value("${mongodb.name}")
//    private String databaseName;

    private QuickTour() {
    }

    public static void main(final String[] args) {
        final Morphia morphia = new Morphia();

        // tell morphia where to find your classes
        // can be called multiple times with different packages or classes
        morphia.mapPackage("xyz.modshell.mongo.demo");

        // create the Datastore connecting to the database running on the default port on the local host
        final Datastore datastore = morphia.createDatastore(new MongoClient(), "morphia_example");
//        datastore.getDB().dropDatabase();
        datastore.getDatabase().drop();
        datastore.ensureIndexes();

        final Employee elmer = new Employee("Elmer Fudd11", 50000.0);
        datastore.save(elmer);

        final Employee daffy = new Employee("Daffy Duck", 40000.0);
        datastore.save(daffy);

        final Employee pepe = new Employee("Pepé Le Pew", 25000.0);
        datastore.save(pepe);

        elmer.getDirectReports().add(daffy);
        elmer.getDirectReports().add(pepe);

        datastore.save(elmer);

        Query<Employee> query = datastore.find(Employee.class);
        final long employees = query.count();

        final List<Employee> result = query.asList();

        Assert.assertEquals(3, employees);

        long underpaid = datastore.find(Employee.class)
                                  .filter("salary <=", 30000)
                                  .count();
        Assert.assertEquals(1, underpaid);

        underpaid = datastore.find(Employee.class)
                             .field("salary").lessThanOrEq(30000)
                             .count();
        Assert.assertEquals(1, underpaid);

        final Query<Employee> underPaidQuery = datastore.find(Employee.class)
                                                        .filter("salary <=", 30000);
        final UpdateOperations<Employee> updateOperations = datastore.createUpdateOperations(Employee.class)
                                                                     .inc("salary", 10000);

        final UpdateResults results = datastore.update(underPaidQuery, updateOperations);

        Assert.assertEquals(1, results.getUpdatedCount());

        final Query<Employee> overPaidQuery = datastore.find(Employee.class)
                                                       .filter("salary >", 100000);
        datastore.delete(overPaidQuery);
    }
}
