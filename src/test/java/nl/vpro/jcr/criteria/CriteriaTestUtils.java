/*
 *
 * Criteria API for Magnolia CMS (http://www.openmindlab.com/lab/products/mgnlcriteria.html)
 * Copyright(C) 2009-2013, Openmind S.r.l. http://www.openmindonline.it
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package nl.vpro.jcr.criteria;


import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import javax.jcr.*;
import javax.jcr.nodetype.NodeTypeManager;
import javax.jcr.nodetype.NodeTypeTemplate;
import javax.jcr.nodetype.PropertyDefinitionTemplate;

import org.apache.commons.io.FileUtils;
import org.apache.jackrabbit.core.TransientRepository;
import org.apache.jackrabbit.core.fs.local.FileUtil;


/**
 * Utility methods used in unit tests.
 * @author fgiust
 */
@Slf4j
public class CriteriaTestUtils {

    static TransientRepository repository;
    static Path tempDirectory;
    static Path tempFile;
    public static Session session;
    public static Node root;


    @SneakyThrows
    public static void setup() {
         // Using jackrabbit memory only seems to be impossible. Sad...
        tempDirectory = Files.createTempDirectory("criteriatest");
        System.setProperty("derby.stream.error.file", new File(tempDirectory.toFile(), "derby.log").toString());
        tempFile = Files.createTempFile("repository", ".xml");
        Files.copy(CriteriaTestUtils.class.getResourceAsStream("/repository.xml"), tempFile, StandardCopyOption.REPLACE_EXISTING);
        FileUtil.delete(tempDirectory.toFile());
        repository = new TransientRepository(tempFile.toFile(), tempDirectory.toFile());;
        session = getSession();
        root = session.getRootNode();
    }

    public static  void shutdown() {
        repository.shutdown();

        try {
            FileUtils.deleteDirectory(tempDirectory.toFile());
            Files.deleteIfExists(tempFile);
        } catch (IOException ioe) {
            log.warn(ioe.getMessage(), ioe);
        }
        log.info("Removed " + tempDirectory + " and " + tempFile);
    }

    public static Session getSession() throws RepositoryException {
        return repository.login(new SimpleCredentials("admin", "admin".toCharArray()));
    }
    public static void defineA() throws RepositoryException {

        NodeTypeManager nodeTypeManager = session.getWorkspace().getNodeTypeManager();
        NodeTypeTemplate a  = nodeTypeManager.createNodeTypeTemplate();
        a.setName("a");
        {
            PropertyDefinitionTemplate media  = nodeTypeManager.createPropertyDefinitionTemplate();
            media.setName("media");
            media.setRequiredType(PropertyType.STRING);
            a.getPropertyDefinitionTemplates().add(media);
        }
        {
            PropertyDefinitionTemplate title  = nodeTypeManager.createPropertyDefinitionTemplate();
            title.setName("title");
            title.setRequiredType(PropertyType.STRING);
            a.getPropertyDefinitionTemplates().add(title);
        }
        {
            PropertyDefinitionTemplate longType   = nodeTypeManager.createPropertyDefinitionTemplate();
            longType.setName("long");
            longType.setRequiredType(PropertyType.LONG);
            a.getPropertyDefinitionTemplates().add(longType);
        }
        {
            PropertyDefinitionTemplate dateType   = nodeTypeManager.createPropertyDefinitionTemplate();
            dateType.setName("date");
            dateType.setRequiredType(PropertyType.DATE);
            a.getPropertyDefinitionTemplates().add(dateType);
        }
        a.setQueryable(true);
        nodeTypeManager.registerNodeType(a, true);
    }
    public static void defineB() throws RepositoryException {

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
