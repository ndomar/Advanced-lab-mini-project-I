/*
 * Copyright 1999-2004 Carnegie Mellon University.
 * Portions Copyright 2004 Sun Microsystems, Inc.
 * Portions Copyright 2004 Mitsubishi Electric Research Laboratories.
 * All Rights Reserved.  Use is subject to license terms.
 *
 * See the file "license.terms" for information on usage and
 * redistribution of this file, and for a DISCLAIMER OF ALL
 * WARRANTIES.
 *
 */

package edu.cmu.sphinx.demo.Calculator1310;

import edu.cmu.sphinx.frontend.util.Microphone;
import edu.cmu.sphinx.recognizer.Recognizer;
import edu.cmu.sphinx.result.Result;
import edu.cmu.sphinx.util.props.ConfigurationManager;
import java.util.Iterator;
import java.util.Map;


/**
 * A simple HelloWorld demo showing a simple speech application built using Sphinx-4. This application uses the Sphinx-4
 * endpointer, which automatically segments incoming audio into utterances and silences.
 */
public class Calculator {
	

	

    public static void main(String[] args) {
        Interface theInterface = new Interface();
        ConfigurationManager cm;

        if (args.length > 0) {
            cm = new ConfigurationManager(args[0]);
        } else {
            cm = new ConfigurationManager(Calculator.class.getResource("Calculator1310.config.xml"));
        }

        Recognizer recognizer = (Recognizer) cm.lookup("recognizer");
        recognizer.allocate();
        SentenceParser sp = new SentenceParser();

        // start the microphone or exit if the programm if this is not possible
        Microphone microphone = (Microphone) cm.lookup("microphone");
        if (!microphone.startRecording()) {
            System.out.println("Cannot start microphone.");
            recognizer.deallocate();
            System.exit(1);
        }

        System.out.println("Calculate Something.");
        // loop the recognition until the programm exits.
        SentenceParser sp = new SentenceParser();
        while (true) {
            System.out.println("Start speaking. Press Ctrl-C to quit.\n");

            Result result = recognizer.recognize();

            if (result != null) {
                String resultText = result.getBestFinalResultNoFiller();
                System.out.println("You said: " + resultText + '\n');
                Double output = sp.parse(resultText);
                //refresh values here
                Iterator it = variableList.entrySet().iterator();
                int i = 0;
                while (it.hasNext()) {
                    Map.Entry pairs = (Map.Entry)it.next();
                    theInterface.variables[i].setText(pairs.getKey() + " = " + pairs.getValue());
                    it.remove(); // avoids a ConcurrentModificationException
                    i = i + 1;
                }
                theInterface.variables[26].setText("Output = " + output);
                theInterface.result.setText("");
                theInterface.result.setText(output + "");
            } else {
                System.out.println("I can't hear what you said.\n");
            }
        }
    }
}
