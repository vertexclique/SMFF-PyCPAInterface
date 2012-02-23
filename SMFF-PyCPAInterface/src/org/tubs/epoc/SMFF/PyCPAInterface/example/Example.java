package org.tubs.epoc.SMFF.PyCPAInterface.example;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.tubs.epoc.SMFF.ImportExport.XML.ModelLoader;
import org.tubs.epoc.SMFF.ImportExport.XML.ModelSaver;
import org.tubs.epoc.SMFF.ModelElements.SystemModel;
import org.tubs.epoc.SMFF.PyCPAInterface.PyCPAAnalysis;

public class Example {

  /**
   * @param args
   */
  public static Logger  logger = Logger.getLogger("org.tubs.epoc.SMFF");

  public static void main(String[] args) {
  logger.setLevel(Level.WARN);
  BasicConfigurator.configure();
    try {
      // create model loader
      ModelLoader modelLoader = new ModelLoader("C:\\Users\\moritzn\\workspaceJava\\SMFF-PyCPAInterface\\src\\org\\tubs\\epoc\\SMFF\\PyCPAInterface\\example\\smff_system.xml");
      // load system model
      SystemModel model = modelLoader.generateSystem();
      // instantiate PyCPA analysis
      PyCPAAnalysis pycpa = new PyCPAAnalysis(model, "C:\\Programme\\Python2.7.2\\python.exe", "C:\\Users\\moritzn\\workspaceJava\\pycpa\\src");
      // analyze
      pycpa.analyze();
      //save
      new ModelSaver("C:\\Users\\moritzn\\workspaceJava\\SMFF-PyCPAInterface\\src\\org\\tubs\\epoc\\SMFF\\PyCPAInterface\\example\\smmf_system_annotated.xml").saveModel(model);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

}
