package feta;
import java.lang.reflect.*;
import feta.actions.*;
import feta.network.DirectedNetwork;
import feta.network.Network;
import feta.network.UndirectedNetwork;
import feta.readnet.*;
import org.json.simple.JSONObject;

import java.util.ArrayList;

/** Class controlling what FETA does */

public class FetaModel {

    private final FetaOptions options_;
    private Network network_;
    private final ArrayList<SimpleAction> actionsToDo_;

    public FetaModel() {
        System.out.println("Launching FETA");
        options_= new FetaOptions();
        actionsToDo_= new ArrayList<SimpleAction>();
    }

    public void readConfigs(String configFile) {
        options_.readConfig(configFile);
    }

    public void execute() {
        parseActionList(options_.actionOps_);
        initialiseNetwork();
        long startTime = System.currentTimeMillis();
        for (SimpleAction act: actionsToDo_) {
            act.setNetwork(network_);
            act.execute();
            long curTime = System.currentTimeMillis();
            System.out.println("Action "+act+ " completed in "+(curTime - startTime)+" milliseconds.");
        }
        long endTime = System.currentTimeMillis();
        System.out.println("Job completed in "+(endTime-startTime)+" milliseconds.");
    }
    public void parseActionList(JSONObject actionList) {

        for (Object o: actionList.keySet()) {
            String singleAction= o.toString();
            SimpleAction action = newAction(singleAction);
            action.parseActionOptions((JSONObject) actionList.get(singleAction));
            action.parseStoppingConditions((JSONObject) actionList.get(singleAction));
            actionsToDo_.add(action);
        }
    }

    public void initialiseNetwork() {
		ReadNet reader;
		try {
			reader = extractReaderType();
        } catch (IllegalArgumentException e) {
			System.out.println("Can't get reader type."+e);
			System.exit(0);
			return; //Java --- grrr... this prevents compilation error.
		}
        boolean typedNet = options_.isTypedNetwork();
        if (options_.isDirectedInput()) {
            network_= new DirectedNetwork(reader,typedNet);
        } else network_= new UndirectedNetwork(reader,typedNet);
        network_.numRecents_=options_.getNoRecents();
        network_.getLinksFromFile();
    }

    /** Reads from a string the relevant action type */
    private SimpleAction newAction(String name) {
        if (name.equals("Measure")) {
            return new Measure();
        } else if (name.equals("Grow")) {
            return new Grow(options_);
        } else if (name.equals("Translate")) {
            return new Translate(options_);
        } else if (name.equals("ParseTest") & !options_.isDirectedInput()) {
            return new ParseTest(options_.isDirectedInput());
        } else if (name.equals("Likelihood")) {
            return new Likelihood(options_);
        } else if (name.equals("NormalisedLikelihood")) {
            return new NormalisedLikelihood(options_);
        } else if (name.equals("FitMixedModel")) {
            return new FitMixedModel(options_);
        }
        else {
            throw new IllegalArgumentException("Unrecognised or missing action name "+name);
        }
    }

    /** Sets which network file reader to do the job */
    private ReadNet extractReaderType() throws IllegalArgumentException {
        ReadNet reader;
        Class <? extends ReadNet> cl= null;
        
        try {
			cl= findClass(options_.getInputType());
			Constructor <?> c= cl.getConstructor(FetaOptions.class);
			reader= (ReadNet)c.newInstance(options_);
		} catch (ClassNotFoundException e) {
			throw new IllegalArgumentException("Parser specifies unknown reader class "+
				options_.getInputType());	
		} catch (Exception e) {
			System.out.println(e);
			throw new IllegalArgumentException("Cannot instantiate class "+
				options_.getInputType());
		}
		
        return reader;
    }

    /** Searches for reader class. Given a class name C it searches
     * in current directory for C.class then for ReadNetC.class
     * then it does the same in feta.readnet directory */
	private Class <? extends ReadNet> findClass(String rawname) throws ClassNotFoundException {
		Class <? extends ReadNet> cl= null;
		String cname=rawname;
		try {
			cl=Class.forName(cname).asSubclass(ReadNet.class);
			return cl;
		} catch (ClassNotFoundException e) {
		}
		cname="ReadNet"+rawname;
		try {
			cl=Class.forName(cname).asSubclass(ReadNet.class);
			return cl;
		} catch (ClassNotFoundException e) {
		}
		cname="feta.readnet"+rawname;
		try {
			cl=Class.forName(cname).asSubclass(ReadNet.class);
			return cl;
		} catch (ClassNotFoundException e) {
		}
		cname="feta.readnet.ReadNet"+rawname;
		try {
			cl=Class.forName(cname).asSubclass(ReadNet.class);
			return cl;
		} catch (ClassNotFoundException e) {
			throw(e);
		}
		
	}

}
