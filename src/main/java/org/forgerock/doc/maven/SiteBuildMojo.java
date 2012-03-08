/*
 * MPL 2.0 HEADER START
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * If applicable, add the following below this MPL 2.0 HEADER, replacing
 * the fields enclosed by brackets "[]" replaced with your own identifying
 * information:
 *     Portions Copyright [yyyy] [name of copyright owner]
 *
 * MPL 2.0 HEADER END
 *
 *     Copyright 2012 ForgeRock AS
 *
 */

package org.forgerock.doc.maven;



import java.io.File;
import java.util.ArrayList;
import java.util.Set;

import org.apache.maven.plugin.MojoExecutionException;
import org.twdata.maven.mojoexecutor.MojoExecutor;



/**
 * Layout built documentation. The resulting documentation set is found under a
 * doc/ directory in the site directory.
 *
 * @goal layout
 * @phase site
 */
public class SiteBuildMojo extends AbstractBuildMojo
{
  /**
   * File system directory for site build
   *
   * @parameter default-value="${project.build.directory}/site"
   *            expression="${siteDirectory}"
   * @required
   */
  protected File siteDirectory;



  /**
   * {@inheritDoc}
   */
  @Override
  public void execute() throws MojoExecutionException
  {
    Executor exec = new Executor();
    getLog().info("doc-build-plugin: Laying out site...");
    exec.layout();
  }



  /**
   * Enclose methods to run plugins.
   */
  class Executor extends MojoExecutor
  {
    /**
     * Returns element specifying built documents to copy to the site directory.
     *
     * @return Compound element specifying built documents to copy
     * @throws MojoExecutionException
     *           Something went wrong getting document names.
     */
    private MojoExecutor.Element getResources()
        throws MojoExecutionException
    {

      ArrayList<MojoExecutor.Element> r = new ArrayList<MojoExecutor.Element>();

      Set<String> docNames = DocUtils.getDocumentNames(
          docbkxSourceDirectory, documentSrcName);
      if (docNames.isEmpty())
      {
        throw new MojoExecutionException("No document names found.");
      }

      for (String docName : docNames)
      {
        String epubDir = docbkxOutputDirectory.getPath().replaceAll(
            File.separator, "/")
            + "/epub/" + docName;
        r.add(
            element(name("resource"),
            element(name("directory"), epubDir),
            element(name("includes"),
                element(name("include"), "**/*.epub"))));
      }

      String htmlDir = docbkxOutputDirectory.getPath().replaceAll(
          File.separator, "/")
          + "/html/";
      r.add(
          element(name("resource"),
          element(name("directory"), htmlDir)));

      String pdfDir = docbkxOutputDirectory.getPath().replaceAll(
          File.separator, "/")
          + "/pdf/";
      r.add(
          element(name("resource"),
          element(name("directory"), pdfDir),
          element(name("includes"),
              element(name("include"), "**/*.pdf"))));

      String rtfDir = docbkxOutputDirectory.getPath().replaceAll(
          File.separator, "/")
          + "/rtf/";
      r.add(
          element( name("resource"),
          element(name("directory"), rtfDir),
          element(name("includes"),
              element(name("include"), "**/*.rtf"))));

      return element("resources", r.toArray(new Element[0]));
    }



    /**
     * Lay out docs in site directory under <code>/doc</code>
     *
     * @throws MojoExecutionException
     *           Problem during execution.
     */
    public void layout() throws MojoExecutionException
    {

      String siteDocDirectory = siteDirectory.getPath().replaceAll(
          File.separator, "/")
          + "/doc";
      executeMojo(
          plugin(
              groupId("org.apache.maven.plugins"),
              artifactId("maven-resources-plugin"),
              version(resourcesVersion)),
          goal("copy-resources"),
          configuration(
              element(name("encoding"), "UTF-8"),
              element(name("outputDirectory"), siteDocDirectory),
              getResources()),
          executionEnvironment(project, session, pluginManager));
    }
  }
}
