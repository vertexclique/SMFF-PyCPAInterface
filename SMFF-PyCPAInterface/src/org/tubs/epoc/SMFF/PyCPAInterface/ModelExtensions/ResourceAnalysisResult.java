package org.tubs.epoc.SMFF.PyCPAInterface.ModelExtensions;

import org.jdom.Element;
import org.tubs.epoc.SMFF.ModelElements.Platform.AbstractResourceData;

public class ResourceAnalysisResult extends AbstractResourceData{
  private Double load;
  
  
  /**
   * Initialize a new constraint at the output with the provided timing behavior value.
   * 
   * @param constraint
   *          constraint through which a PJdConstraint instance will be created.
   */
  public ResourceAnalysisResult(Double load) {
    super();
    this.load = load;
  }
  
  /**
   * new jitter constraint at the output created from a jdom XML element
   * @param element
   */
  public ResourceAnalysisResult(Element element){
    super();
    String loadString = element.getAttributeValue("load");
    if(loadString!=null){
      this.load = Double.valueOf(loadString);
    }
  }

  public Double getLoad() {
    return load;
  }
}
