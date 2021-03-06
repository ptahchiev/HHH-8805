package demo;

import demo.model.AbstractEntityModel;
import demo.model.AbstractFilterModel;
import demo.model.AbstractFilteredEntityModel;
import demo.model.CategoryModel;
import demo.model.DesktopFilter;
import demo.model.MobileFilter;
import demo.model.ProductModel;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.boot.spi.MetadataImplementor;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.hibernate.tool.hbm2ddl.Target;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Arrays;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Hhh8805Application.class)
public class Hhh8805ApplicationTests {

    @PersistenceContext(name = "entityManagerFactory")
    private EntityManager entityManager;

    @Resource(name = "transactionManager")
    private PlatformTransactionManager transactionManager;

    private TransactionTemplate transactionTemplate;

    @Test
    public void contextLoads() {
        this.transactionTemplate = new TransactionTemplate(transactionManager);
        this.transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                MobileFilter mobileFilter = new MobileFilter();
                mobileFilter.setPk(1L);

                entityManager.persist(mobileFilter);
            }
        });

        final DesktopFilter desktopFilter = new DesktopFilter();
        desktopFilter.setPk(2L);

        this.transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                entityManager.persist(desktopFilter);
            }
        });

        this.transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                ProductModel productModel = new ProductModel();
                productModel.setPk(1L);
                productModel.setFilters(Arrays.<AbstractFilterModel>asList(desktopFilter));
                entityManager.persist(productModel);
            }
        });

    }

    @Test
    public void test() {

        final SchemaExport export = new SchemaExport(getHibernateConfiguration());
        export.setDelimiter(";");
        export.setFormat(true);
        export.setOutputFile("schema.sql");
        export.execute(Target.EXPORT, SchemaExport.Type.BOTH);
    }

    public MetadataImplementor getHibernateConfiguration() {
        StandardServiceRegistryBuilder registryBuilder = new StandardServiceRegistryBuilder();

        registryBuilder.applySetting("hibernate.dialect", "org.hibernate.dialect.HSQLDialect");
        registryBuilder.applySetting("hibernate.connection.driver_class", "org.hsqldb.jdbcDriver");
        registryBuilder.applySetting("hibernate.connection.username", "sa");
        registryBuilder.applySetting("hibernate.connection.password", "");
        registryBuilder.applySetting("hibernate.connection.url", "jdbc:hsqldb:mem:solarapparel;sql.enforce_strict_size=true;hsqldb.tx=mvcc");

        StandardServiceRegistry standardRegistry = registryBuilder.build();
        MetadataSources sources = new MetadataSources(standardRegistry);

        sources.addAnnotatedClass(AbstractEntityModel.class);
        sources.addAnnotatedClass(AbstractFilteredEntityModel.class);
        sources.addAnnotatedClass(AbstractFilterModel.class);
        sources.addAnnotatedClass(CategoryModel.class);
        sources.addAnnotatedClass(ProductModel.class);
        sources.addAnnotatedClass(DesktopFilter.class);
        sources.addAnnotatedClass(MobileFilter.class);

        return (MetadataImplementor) sources.buildMetadata();
    }
}
