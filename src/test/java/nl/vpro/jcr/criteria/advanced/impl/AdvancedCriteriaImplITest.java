package nl.vpro.jcr.criteria.advanced.impl;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import nl.vpro.jcr.criteria.query.AdvancedResultItem;
import nl.vpro.jcr.criteria.query.Criteria;
import nl.vpro.jcr.criteria.query.JCRCriteriaFactory;
import nl.vpro.jcr.criteria.query.criterion.Disjunction;
import nl.vpro.jcr.criteria.query.criterion.MatchMode;
import nl.vpro.jcr.criteria.query.criterion.Order;
import nl.vpro.jcr.criteria.query.criterion.Restrictions;
import org.apache.commons.io.FileUtils;
import org.apache.jackrabbit.core.TransientRepository;
import org.apache.jackrabbit.core.fs.local.FileUtil;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.jcr.*;
import javax.jcr.nodetype.NodeType;
import javax.jcr.nodetype.NodeTypeManager;
import javax.jcr.nodetype.NodeTypeTemplate;
import javax.jcr.nodetype.PropertyDefinitionTemplate;
import javax.jcr.query.Query;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static nl.vpro.jcr.criteria.query.JCRCriteriaFactory.createCriteria;
import static org.testng.Assert.assertTrue;
import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertFalse;


/**
 * @author Michiel Meeuwissen
 * @since 1.1
 */

@Slf4j
public class AdvancedCriteriaImplITest {


    Repository repository;
    Path tempDirectory;
    Path tempFile;
    Session session;
    Node root;



    @BeforeMethod
    public void setup() throws IOException, RepositoryException {
        // Using jackrabbit memory only seems to be impossible. Sad...
        tempDirectory = Files.createTempDirectory("criteriatest");
        System.setProperty("derby.stream.error.file", new File(tempDirectory.toFile(), "derby.log").toString());
        tempFile = Files.createTempFile("repository", ".xml");
        Files.copy(getClass().getResourceAsStream("/repository.xml"), tempFile, StandardCopyOption.REPLACE_EXISTING);
        FileUtil.delete(tempDirectory.toFile());
        repository = new TransientRepository(tempFile.toFile(), tempDirectory.toFile());;
        session = getSession();
        root = session.getRootNode();
        { // define a
            NodeTypeManager nodeTypeManager = session.getWorkspace().getNodeTypeManager();
            NodeTypeTemplate a  = nodeTypeManager.createNodeTypeTemplate();
            a.setName("a");
            PropertyDefinitionTemplate media  = nodeTypeManager.createPropertyDefinitionTemplate();
            media.setName("media");
            media.setRequiredType(PropertyType.STRING);
            a.getPropertyDefinitionTemplates().add(media);
            a.setQueryable(true);
            nodeTypeManager.registerNodeType(a, true);
        }
        { // define b

            NodeTypeManager nodeTypeManager = session.getWorkspace().getNodeTypeManager();
            NodeTypeTemplate b  = nodeTypeManager.createNodeTypeTemplate();
            b.setName("b");
            PropertyDefinitionTemplate media  = nodeTypeManager.createPropertyDefinitionTemplate();
            media.setName("media");
            media.setRequiredType(PropertyType.STRING);
            b.getPropertyDefinitionTemplates().add(media);
            b.setQueryable(true);
            nodeTypeManager.registerNodeType(b, true);
        }

    }
    @AfterMethod
    public void shutdown() {
        try {
            FileUtils.deleteDirectory(tempDirectory.toFile());
            Files.deleteIfExists(tempFile);
        } catch (IOException ioe) {
            log.warn(ioe.getMessage(), ioe);
        }
        log.info("Removed " + tempDirectory + " and " + tempFile);
    }

    @Test
    public void start() throws RepositoryException {
        {
            Node hello = root.addNode("hello");
            hello.setProperty("a", "a1");
            Node hello2 = root.addNode("hello2");
            hello2.setProperty("a", "b");
            Node goodbye = root.addNode("bye");
            goodbye.setProperty("a", "a2");
            session.save();
        }
        {
            Criteria criteria =
                createCriteria()
                    .setBasePath("/")
                    .add(Restrictions.attrLike("a", "a", MatchMode.START))
                    ;
                ;
            check(criteria, 2);
        }
    }

    @Test
    public void booleanMatch() throws RepositoryException {
        {
            Node hello = root.addNode("hello");
            hello.setProperty("a", Boolean.TRUE);
            Node hello2 = root.addNode("hello2");
            hello2.setProperty("a", Boolean.FALSE);
            Node goodbye = root.addNode("bye");
            goodbye.setProperty("a", Boolean.TRUE);
            Node goodbye2 = root.addNode("bye2");
            session.save();
        }



        {
            check(
                createCriteria()
                    .setBasePath("/")
                    .add(Restrictions.attrEq("a", Boolean.TRUE)),
                2);

                ;
            check(createCriteria()
                            .setBasePath("/")
                    .add(Restrictions.eq("a", Boolean.FALSE)),
                3); // hello2, byte2, root
        }
    }

    @Test
    public void isNull() throws RepositoryException {
        {
            Node hello = root.addNode("hello");
            hello.setProperty("a", Boolean.TRUE);
            Node hello2 = root.addNode("hello2");
            hello2.setProperty("a", Boolean.FALSE);
            Node goodbye = root.addNode("bye");
            goodbye.setProperty("a", Boolean.TRUE);
            Node goodbye2 = root.addNode("bye2");
            session.save();
        }
        {
            check(createCriteria()
                            .add(Restrictions.isNotNull("@a")),
                3);
            check(
                createCriteria()
                    .add(Restrictions.isNull("@a")),
                2); // goodbye and root

                ;

        }
    }

    @Test
    public void someUseCase() throws RepositoryException {
        {
            Node node1 = root.addNode("node1");
            node1.setProperty("media", "ce297ce9-aaa5-43d5-be44-41045e782708");
            node1.setPrimaryType("a");
            session.save();
        }

        String basepath = "/jcr:root/*";

        Criteria criteria = JCRCriteriaFactory
            .createCriteria()
            .setBasePath(basepath)
            .addOrder(Order.desc("@jcr:score"));


        List<String> nt= Arrays.asList("a", "b");
        Disjunction nodetypes = Restrictions.disjunction();
        for (String string : nt) {
            nodetypes.add(Restrictions.eq("@jcr:primaryType", string));
        }
        criteria.add(nodetypes);

        Disjunction properties = Restrictions.disjunction();
        List<String> prop = Arrays.asList("media", "group");
        UUID uuid = UUID.fromString("ce297ce9-aaa5-43d5-be44-41045e782708");
        for (String string : prop) {
            properties.add(Restrictions.contains(string, uuid));
        }
        criteria.add(properties);

        check(criteria, 1);

    }
    @SneakyThrows
    void check(Criteria criteria, int expectedSize) {
        AdvancedResultImpl result = (AdvancedResultImpl) criteria.execute(session);
        for (AdvancedResultItem item : result.getItems()) {
            boolean isUnstructured = item.getJCRNode().isNodeType(NodeType.NT_UNSTRUCTURED);
            log.info("{} {} (is unstructured: {})", item.getJCRNode().getPrimaryNodeType().getName(), item, isUnstructured);
        }
        assertFalse(result.totalSizeDetermined());
        assertEquals(expectedSize, result.getTotalSize());
        assertTrue(result.totalSizeDetermined());

    }


    AdvancedResultImpl directXpath(String expression) {
        return QueryExecutorHelper.execute(
            expression,
            Query.XPATH,
            session,
            0,
            0,
                null
            );

    }

    Session getSession() throws RepositoryException {
        return repository.login(new SimpleCredentials("admin", "admin".toCharArray()));
    }



}
