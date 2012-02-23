package org.tubs.epoc.SMFF.PyCPAInterface;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;
import org.tubs.epoc.SMFF.ModelElements.SystemModel;
import org.tubs.epoc.SMFF.ModelElements.Application.ApplicationModel;
import org.tubs.epoc.SMFF.ModelElements.Application.PJdTimingBehavior;
import org.tubs.epoc.SMFF.ModelElements.Application.Task;
import org.tubs.epoc.SMFF.ModelElements.Application.TaskIdentifier;
import org.tubs.epoc.SMFF.ModelElements.Application.TaskLink;
import org.tubs.epoc.SMFF.ModelElements.Platform.CommResource;
import org.tubs.epoc.SMFF.ModelElements.Platform.Resource;
import org.tubs.epoc.SMFF.ModelElements.Timing.AbstractActivationPattern;
import org.tubs.epoc.SMFF.ModelElements.Timing.PJActivation;
import org.tubs.epoc.SMFF.PyCPAInterface.ModelExtensions.ResourceAnalysisResult;

public class AnalysisResultParser {
  private File analyzedModel;
  private SystemModel model;

  /**
   * 
   * @param model - SMFF system model to annotate with the analysis results
   * @param analysisResults - SMFF system File containing the annotation of analysis results from PyCPA
   */
  public AnalysisResultParser(SystemModel model, File analyzedModel) {
    super();
    this.analyzedModel = analyzedModel;
    this.model = model;
  }

  /**
   * reads the analysis result to the system model
   * @param model
   * @throws IOException 
   * @throws JDOMException 
   */
  public void readResults() throws JDOMException, IOException{
    // parse the XML file using SAX
    SAXBuilder builder = new SAXBuilder();
    FileInputStream inputStream = new FileInputStream(analyzedModel);
    Document document = builder.build(inputStream);
    // retrieve the root element and store it in this factory
    Element systemElement = document.getRootElement();
    Element analysisResults = systemElement.getChild("Analysis");

    // get results of resources
    Element resourcesResults = analysisResults.getChild("Resources");
    @SuppressWarnings("unchecked")
    List<Element> resList = resourcesResults.getChildren("Resource");
    for (Element resResult : resList) {
      String resString = resResult.getAttributeValue("ID");
      Resource res = model.getResource(Integer.valueOf(resString));

      res.addExtData(new ResourceAnalysisResult(resResult), false, true, false);
    }
    @SuppressWarnings("unchecked")
    List<Element> cresList = resourcesResults.getChildren("CommResource");
    for (Element cresResult : cresList) {
      String cresString = cresResult.getAttributeValue("ID");
      CommResource res = model.getCommResource(Integer.valueOf(cresString));

      res.addExtData(new ResourceAnalysisResult(cresResult), false, true, false);
    }

    // get results of schedulable elements
    Element applicationsResults = analysisResults.getChild("Applications");
    @SuppressWarnings("unchecked")
    List<Element> appList = applicationsResults.getChildren("Application");
    for (Element appElem : appList) {
      String appString = appElem.getAttributeValue("appID");
      String appVString = appElem.getAttributeValue("appV");
      int appPeriod = 0;

      // get activation period of some source task (will be used for output behavior of all tasks)
      ApplicationModel app = model.getApplication(Integer.valueOf(appString));
      for(Task task : app.getTaskList().values()){
        if(task.getTrgLinkList().size()==0){
          AbstractActivationPattern actPattern = task.getActiveProfile().getActivationPattern();
          if(actPattern instanceof PJActivation){
            appPeriod = ((PJActivation)actPattern).getActivationPeriod();
            break;
          }
        }
      }

      // results of tasks
      @SuppressWarnings("unchecked")
      List<Element> taskList = resourcesResults.getChildren("Task");
      for (Element taskResult : taskList) {
        String taskString = taskResult.getAttributeValue("ID");
        TaskIdentifier taskId = new TaskIdentifier(Integer.valueOf(appString), Integer.valueOf(appVString), Integer.valueOf(taskString));
        Task task = model.getTask(taskId);


        // get bcrt and wcrt
        String bcrtString = taskResult.getAttributeValue("BCRT");
        task.setBCRT(Integer.valueOf(bcrtString));
        String wcrtString = taskResult.getAttributeValue("WCRT");
        task.setWCRT(Integer.valueOf(wcrtString));

        // get jitter values
        String inJitterString = taskResult.getAttributeValue("input_jitter");
        String outJitterString = taskResult.getAttributeValue("output_jitter");
        int inJitter = Integer.valueOf(inJitterString);
        task.setInputBehavior(new PJdTimingBehavior(appPeriod, inJitter, 0));
        int outJitter = Integer.valueOf(outJitterString);
        task.setOutputBehavior(new PJdTimingBehavior(appPeriod, outJitter, 0));
      }


      // results of taskslinks
      @SuppressWarnings("unchecked")
      List<Element> tasklinkList = resourcesResults.getChildren("Tasklink");
      for (Element tasklinkResult : tasklinkList) {
        String tasklinkString = tasklinkResult.getAttributeValue("ID");
        TaskLink tasklink = app.getTaskLink(Integer.valueOf(tasklinkString));


        // get bcrt and wcrt
        String bcrtString = tasklinkResult.getAttributeValue("BCRT");
        tasklink.setBCRT(Integer.valueOf(bcrtString));
        String wcrtString = tasklinkResult.getAttributeValue("WCRT");
        tasklink.setWCRT(Integer.valueOf(wcrtString));

        // get jitter values
        String inJitterString = tasklinkResult.getAttributeValue("input_jitter");
        String outJitterString = tasklinkResult.getAttributeValue("output_jitter");
        int inJitter = Integer.valueOf(inJitterString);
        tasklink.setInputBehavior(new PJdTimingBehavior(appPeriod, inJitter, 0));
        int outJitter = Integer.valueOf(outJitterString);
        tasklink.setOutputBehavior(new PJdTimingBehavior(appPeriod, outJitter, 0));
      }
    }
    inputStream.close();
  }
}
