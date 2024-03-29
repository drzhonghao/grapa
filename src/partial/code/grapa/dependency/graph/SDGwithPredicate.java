package partial.code.grapa.dependency.graph;

import java.util.Iterator;
import java.util.stream.Stream;

import com.ibm.wala.cast.java.ipa.modref.AstJavaModRef;
import com.ibm.wala.ipa.callgraph.CallGraph;
import com.ibm.wala.ipa.callgraph.propagation.InstanceKey;
import com.ibm.wala.ipa.callgraph.propagation.PointerAnalysis;

import com.ibm.wala.ipa.slicer.SDG;
import com.ibm.wala.ipa.slicer.Statement;
import com.ibm.wala.ipa.slicer.Slicer.ControlDependenceOptions;
import com.ibm.wala.ipa.slicer.Slicer.DataDependenceOptions;

import com.ibm.wala.util.collections.IteratorUtil;
import com.ibm.wala.util.debug.Assertions;
import com.ibm.wala.util.graph.Graph;
import com.ibm.wala.util.graph.NodeManager;

public class SDGwithPredicate implements Graph<Statement>{

	private SDG g;
	private Predicate p;
	int nodeCount = -1;

	public SDGwithPredicate(SDG g, Predicate p) {
		// TODO Auto-generated constructor stub
		this.g = g;
		this.p = p;
	}

	 @Override
     public Iterator<Statement> iterator() {
       return Predicate.filter(g.iterator(), p).iterator();
     }

     @Override
     public int getNumberOfNodes() {
       if (nodeCount == -1) {
         nodeCount = IteratorUtil.count(iterator());
       }
       return nodeCount;
     }

     @Override
     public void addNode(Statement n) {
       Assertions.UNREACHABLE();
     }

     @Override
     public void removeNode(Statement n) {
       Assertions.UNREACHABLE();
     }

     @Override
     public boolean containsNode(Statement n) {
       return p.test(n) && g.containsNode(n);
     }

     @Override
     public Iterator<Statement> getPredNodes(Statement n) {
       return Predicate.filter(g.getPredNodes(n), p).iterator();
     }

     @Override
     public int getPredNodeCount(Statement n) {
       return IteratorUtil.count(getPredNodes(n));
     }

     @Override
     public Iterator<Statement> getSuccNodes(Statement n) {
       return Predicate.filter(g.getSuccNodes(n), p).iterator();
     }

    
     
     @Override
     public int getSuccNodeCount(Statement N) {
       return IteratorUtil.count(getSuccNodes(N));
     }

     @Override
     public void addEdge(Statement src, Statement dst) {
       Assertions.UNREACHABLE();
     }

     @Override
     public void removeEdge(Statement src, Statement dst) {
       Assertions.UNREACHABLE();
     }

     @Override
     public void removeAllIncidentEdges(Statement node) {
       Assertions.UNREACHABLE();
     }

     @Override
     public void removeIncomingEdges(Statement node) {
       Assertions.UNREACHABLE();
     }

     @Override
     public void removeOutgoingEdges(Statement node) {
       Assertions.UNREACHABLE();
     }

     @Override
     public boolean hasEdge(Statement src, Statement dst) {
       return g.hasEdge(src, dst) && p.test(src) && p.test(dst);
     }

	@Override
	public void removeNodeAndEdges(Statement n)
			throws UnsupportedOperationException {
		// TODO Auto-generated method stub
		Assertions.UNREACHABLE();
	}

	@Override
	public Stream<Statement> stream() {
		// TODO Auto-generated method stub
		return null;
	}


//	public void reConstruct(DataDependenceOptions dOptions, ControlDependenceOptions cOptions) {
//		g.reConstruct(dOptions, cOptions);
//	}


}
