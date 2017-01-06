package partial.code.grapa.tool;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;

import edu.uci.ics.jung.graph.DirectedSparseGraph;
import partial.code.grapa.delta.graph.DeltaEdge;
import partial.code.grapa.delta.graph.DeltaNode;

public class FileUtils {
	 public static String getContent(File file) {
        char[] b = new char[1024];
        StringBuilder sb = new StringBuilder();
        try {
            FileReader reader = new FileReader(file);
            int n = reader.read(b);
            while (n > 0) {
                sb.append(b, 0, n);
                n = reader.read(b);
            }
            reader.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return sb.toString();
	}

	public static void writeToXmlFile(DirectedSparseGraph<DeltaNode, DeltaEdge> graph, String filename) {
		// TODO Auto-generated method stub
		XStream xstream = new XStream(new StaxDriver());
		try{
			 File file = new File(filename);
			 FileWriter writer=new FileWriter(file);
			 String content = xstream.toXML(graph);
			 writer.write(content);
			 writer.close();
		} catch (IOException e){
		 e.printStackTrace();
		}
	}
}
