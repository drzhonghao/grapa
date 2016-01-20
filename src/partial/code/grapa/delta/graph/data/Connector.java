package partial.code.grapa.delta.graph.data;
public class Connector extends AbstractNode{
	public String type;
	
	public Connector(String type, String label, int side) {
		// TODO Auto-generated constructor stub
		this.type = type;
		this.label = label;
		this.side = side;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return getAbstractLabel();
	}

	public String getAbstractLabel() {
		// TODO Auto-generated method stub
//		String shortLabel;
//		if(this.label.indexOf("throw")>=0){
//			shortLabel = "throw";
//		}else if(this.label.indexOf("return")>=0){
//			shortLabel = "return";
//		}else if(this.label.indexOf("arraylength")>=0){
//			shortLabel = "arraylength";
//		}else if(this.label.indexOf("load_metadata")>=0){
//			shortLabel = "load_metadata";
//		}else if(this.label.indexOf("getService")>=0){
//			shortLabel = "getService";
//		}else if(this.label.indexOf("arrayload")>=0){
//			shortLabel = "arrayload";
//		}else if(this.label.indexOf("enclosing")>=0){
//			shortLabel = "enclosing";
//		}else if(this.label.indexOf("switch")>=0){
//			shortLabel = "switch";
//		}else if(this.label.indexOf("updateBundles")>=0){
//			shortLabel = "updateBundles";
//		}else if(this.label.indexOf("uninstallBundle")>=0){
//			shortLabel = "uninstallBundle";
//		}else if(this.label.indexOf("installIsolatedBundles")>=0){
//			shortLabel = "installIsolatedBundles";
//		}else if(this.label.indexOf("generateFilter")>=0){
//			shortLabel = "generateFilter";
//		}else if(this.label.indexOf("internalGet")>=0){
//			shortLabel = "internalGet";
//		}else if(this.label.indexOf("internalSet")>=0){
//			shortLabel = "internalSet";
//		}else if(this.label.indexOf("stopBundle")>=0){
//			shortLabel = "stopBundle";
//		}else if(this.label.indexOf("startBundle")>=0){
//			shortLabel = "startBundle";
//		}else if(this.label.indexOf("getBundleContext")>=0){
//			shortLabel = "getBundleContext";
//		}else if(this.label.indexOf("getProperties")>=0){
//			shortLabel = "getProperties";
//		}else if(this.label.indexOf("destroy")>=0){
//			shortLabel = "destroy";
//		}else if(this.label.indexOf("parseManifest")>=0){
//			shortLabel = "parseManifest";
//		}else if(this.label.indexOf("create:monitorexit")>=0){
//			shortLabel = "create:monitorexit";
//		}else if(this.label.indexOf("create:monitorenter")>=0){
//			shortLabel = "create:monitorenter";
//		}else if(this.label.indexOf("createInstances")>=0){
//			shortLabel = "createInstances";
//		}else if(this.label.indexOf("quiesce")>=0){
//			shortLabel = "quiesce";
//		}else if(this.label.indexOf("decrementActiveCalls")>=0){
//			shortLabel = "decrementActiveCalls";
//		}else if(this.label.indexOf("createURLContext")>=0){
//			shortLabel = "createURLContext";
//		}else if(this.label.indexOf("findObjectFactoryByClassName")>=0){
//			shortLabel = "findObjectFactoryByClassName";
//		}else{
//			shortLabel = type;
//			int mark = shortLabel.lastIndexOf(".");
//			if(mark>0){
//				shortLabel = shortLabel.substring(mark+1);
//			}
//		}
		
		String shortLabel = label;
		if(shortLabel.startsWith("NORMAL:")||shortLabel.startsWith("NORMAL ")){
			int mark = shortLabel.indexOf(" ");
			shortLabel = shortLabel.substring(mark+1);
			mark = shortLabel.indexOf(" ");
			shortLabel = shortLabel.substring(0, mark);
		}else if(!type.endsWith("NormalStatement")){
			shortLabel = type;
		}
		
		int mark = shortLabel.lastIndexOf(".");
		if(mark>0){
			shortLabel = shortLabel.substring(mark+1);
		}
		
		mark = shortLabel.indexOf(":");
		if(mark>0){
			String value = shortLabel.substring(mark+1);
			try{
				Integer.parseInt(value);
				shortLabel = shortLabel.substring(0, mark);
			}catch(Exception e){
				
			}
		}
		
		
		return shortLabel;
	}
}
