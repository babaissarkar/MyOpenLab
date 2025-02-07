/*
MyOpenLab by Carmelo Salafia www.myopenlab.de
Copyright (C) 2004  Carmelo Salafia cswi@gmx.de

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed lin the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package VisualLogic;

import MyParser.OpenVariable;
import MyParser.Parser;
import Peditor.PropertyEditor;
import SimpleFileSystem.FileSystemInput;
import SimpleFileSystem.FileSystemOutput;

import VisualLogic.variables.VS1DDouble;
import VisualLogic.variables.VSBoolean;
import VisualLogic.variables.VSColor;
import VisualLogic.variables.VSComboBox;
import VisualLogic.variables.VSDouble;
import VisualLogic.variables.VSFlowInfo;
import VisualLogic.variables.VSImage;
import VisualLogic.variables.VSInteger;
import VisualLogic.variables.VSObject;
import VisualLogic.variables.VSString;
import VisualLogic.gui.*;

import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.awt.event.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.security.MessageDigest;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.swing.*;

class VariableNotifyRecord {

    public ExternalIF element;
    public String varName;

    public VariableNotifyRecord(ExternalIF element, String varName) {
        this.element = element;
        this.varName = varName;
    }

}

/**
 *
 * @author Homer
 */
public class Basis extends Object implements ElementIF, VSBasisIF {

    public VSColor backDisabledColor = new VSColor(Color.yellow);
    public DataHistory dataHistory = new DataHistory();
    public String elementPaletteCircuitElementsOldPath = "";
    public String elementPaletteFrontElementsOldPath = "";
    public FrmTestpoints dialogTestpoint;
    public FrameBooleanGraph frameBooleanGraph;
    public FrameDoubleGraph frameDoubleGraph;
    public FrameConsoleOutput console;
    public String vmPassword = "";
    
    public VMEditorPanel ownerVMPanel = null;
    public FrameRunning frm;
    public FrameMain frameCircuit;
    public String projectPath = "";
    public boolean startInFrontMode = false;
    public boolean started = false;
    public boolean debugMode = false;
    public boolean unDecorated = false;
    public boolean showFrontPanelWhenStart = true;
    
    public ArrayList variablenListe = new ArrayList();
    public boolean showToolBar = false;
    public Parser parser = new Parser(this);
    public BufferedImage selectionImage = null;
    public boolean vmProtected = false;
    public JLabel panelLabel = null;
    
    private int dedugDelay = 10; // Speed from 0 to 100 : 0 = fast ; 100 =slow
    private JPanel oldPanelFront = null;
    private final VMObject circuitBasis;
    private final VMObject frontBasis;
    private final ArrayList variableNotifyList = new ArrayList();
    private String XfileName = "";
    
    public int delay = 0; // Speed from 0 to 100 : 0 = fast ; 100 =slow
    public boolean AlwaysOnTop=false;
    public VSComboBox WindowsPosition=new VSComboBox();
    public int CustomXwindowPos=0;
    public int CustomYwindowPos=0;
    public Stack stack = new Stack();

    ScriptEngineManager engineManager;
    ScriptEngine engine;

    Bindings bindings;

    public int getDebugDelay() {
        return dedugDelay;
    }

    public void setDebugDelay(int value) {
        dedugDelay = value;
    }

    @Override
    public boolean varNameExist(String varname) {

        varname = varname.trim();
        OpenVariable node;
        for (int i = 0; i < variablenListe.size(); i++) {
            node = (OpenVariable) variablenListe.get(i);

            if (varname.equals(node.name)) {
                return true;
            }
        }
        return false;
    }

    public void vsClearVars() {
        // NOP
    }

    private final ArrayList liste = new ArrayList();

    private boolean istEintragsBereitsVorhanden(String eintrag) {
        for (int i = 0; i < liste.size(); i++) {
            String val = (String) liste.get(i);

            if (val.equalsIgnoreCase(eintrag)) {
                return true;
            }
        }
        return false;
    }

    private boolean existierenDoppelteNodeTitles() {
        liste.clear();

        Element[] tpNodes = getCircuitBasis().getAllTestpointElements();

        String strName = "";
        for (Element tpNode : tpNodes) {
            strName = tpNode.jGetCaption();
            if (!istEintragsBereitsVorhanden(strName)) {
                liste.add(strName);
            } else {
                return true;
            }
        }
        return false;
    }

    @Override
    public void vsAddTestpointValue(ExternalIF element, VSObject in) {
        if (frameCircuit != null && isLoading() == false) {

            if (existierenDoppelteNodeTitles()) {
                Tools.jException(this, "There are Testpoint Nodes with same name!");
            }

            if (dialogTestpoint != null) {
                dialogTestpoint.addValue(element.jGetCaption(), in.toString());

                if (in instanceof VSBoolean) {
                    VSBoolean val = (VSBoolean) in;

                    frameBooleanGraph.addValue(element.jGetCaption(), val.getValue());
                } else if (in instanceof VSDouble) {
                    VSDouble val = (VSDouble) in;

                    frameDoubleGraph.addValue(element.jGetCaption(), val.getValue());
                }
            }
        }
    }

    @Override
    public String[] vsGetVMInfo() {
        String[] result = new String[3];
        result[0] = autorName;
        result[1] = autorMail;
        result[2] = autorWWW;

        return result;
    }

    public ButtonGroup buttonGroups[] = new ButtonGroup[20];

    public void initButtonGroups() {
        for (int i = 0; i < buttonGroups.length; i++) {
            buttonGroups[i] = new ButtonGroup();
        }
    }

    public void addJButtonToButtonGroup(AbstractButton button, int group) {
        buttonGroups[group].add(button);
    }

    @Override
    public ExternalIF[] vsGetListOfPanelElements() {
        int count = getFrontBasis().getElementCount();
        ExternalIF[] result = new ExternalIF[count];

        for (int i = 0; i < count; i++) {
            result[i] = (ExternalIF) getFrontBasis().getElement(i);
        }

        return result;

    }

    @Override
    public int vsGetVariableDT(String varname) {
        OpenVariable o = getVariable(varname);
        if (o != null) {
            return o.datatype;
        } else {
            return -1;
        }
    }

    private boolean isNum(String val) {
        try {
            double x = Double.parseDouble(val);
            return true;
        } catch (NumberFormatException ex) {
            return false;
        }

    }

    public boolean isString(String val) {
        val = val.trim();

        int i = 0;
        String ch = val.substring(i, i + 1);
        if (ch.equalsIgnoreCase("\"")) {
            i = val.length() - 1;
            ch = val.substring(i, i + 1);
            return ch.equalsIgnoreCase("\"");
        } else {
            return false;
        }
    }

    public boolean isBoolean(String val) {
        val = val.trim();

        return val.equalsIgnoreCase("TRUE") || val.equalsIgnoreCase("FALSE");
    }

    @Override
    public String[] vsGetVariablesNames() {
        String[] result = new String[variablenListe.size()];
        OpenVariable node;
        for (int i = 0; i < variablenListe.size(); i++) {
            node = (OpenVariable) variablenListe.get(i);

            result[i] = node.name;
        }

        return result;
    }

    @Override
    public Object[] vsGetVariablesValues() {
        Object[] result = new Object[variablenListe.size()];
        OpenVariable node;
        for (int i = 0; i < variablenListe.size(); i++) {
            node = (OpenVariable) variablenListe.get(i);

            result[i] = node.value;
        }

        return result;
    }

    // Kopiert den Inhalt eines bekannten VSObjects
    // in einer Variable
    // liefert True wenn alles ok
    // und False wenn die Typen nicht kompatibel
    // oder Variable nicht gefunden.
    @Override
    public boolean vsCopyVSObjectToVariable(Object vsobject, String varname) {
        boolean result = false;

        OpenVariable node = getVariable(varname.trim());
        if (node != null) {
            if (vsobject instanceof VSDouble && node.value instanceof Double) {
                VSDouble v = (VSDouble) vsobject;
                node.value = v.getValue();
                result = true;
            } else if (vsobject instanceof VSString && node.value instanceof String) {
                VSString v = (VSString) vsobject;
                node.value = v.getValue();
                result = true;
            } else if (vsobject instanceof VSBoolean && node.value instanceof Boolean) {
                VSBoolean v = (VSBoolean) vsobject;
                node.value = v.getValue();
                result = true;
            } else if (vsobject instanceof VSInteger && node.value instanceof Integer) {
                VSInteger v = (VSInteger) vsobject;
                node.value = v.getValue();
                result = true;
            }
        }

        bindVars();

        notifyAllElements(varname);
        return result;
    }

    // Kopiert den Inhalt eines einer Variable
    // in ein bekannten VSObjects
    // liefert True wenn alles ok
    // und False wenn die Typen nicht kompatibel
    // oder Variable nicht gefunden.
    @Override
    public boolean vsCopyVariableToVSObject(String varname, Object vsobject) {
        OpenVariable node = getVariable(varname);

        boolean res = false;

        if (node != null) {
            
            if (vsobject instanceof VSInteger && node.value instanceof Integer) {
                Integer v1 = (Integer) node.value;
                VSInteger v2 = (VSInteger) vsobject;
                v2.setValue(v1);
                res = true;
            }             
            if (vsobject instanceof VSDouble && node.value instanceof Double) {
                Double v1 = (Double) node.value;
                VSDouble v2 = (VSDouble) vsobject;
                v2.setValue(v1);
                res = true;
            } 
            if (vsobject instanceof VSString && node.value instanceof String) {
                String v1 = (String) node.value;
                VSString v2 = (VSString) vsobject;
                v2.setValue(v1);
                res = true;
            } if (vsobject instanceof VSBoolean && node.value instanceof Boolean) {
                Boolean v1 = (Boolean) node.value;
                VSBoolean v2 = (VSBoolean) vsobject;
                v2.setValue(v1);
                res = true;
            }
            if (vsobject instanceof VS1DDouble && node.value instanceof VS1DDouble) {
                VS1DDouble v1 = (VS1DDouble) node.value;
                VS1DDouble v2 = (VS1DDouble) vsobject;
                v2.setValue(v1.getValue());
                res = true;
            }
        }

        return res;
    }

    // Liefert den DT in form einer neuen Objectvariable wie Double, Boolean, String
    // inklusive den Vert aus strValue
    // Liefert null wenn ein Fehler im Ausdruck vorhanden ist.
    @Override
    public Object vsGetTypeOF(String strValue) {
        if (isString(strValue)) {
            strValue = strValue.substring(1, strValue.length() - 1);
            return strValue;
        } else if (isBoolean(strValue)) {
            return Boolean.valueOf(strValue);
        } else if (isNum(strValue)) {
            return Double.valueOf(strValue);
        } else {
            OpenVariable o = getVariable(strValue);
            if (o != null) {
                return o.value;
            } else {
                return null;
            }
        }
    }

    // vergleicht zewi werte anhand des operators miteinander.
    @Override
    public boolean vsCompareValues(Object a, Object b, String operator) {
        if (a instanceof Double && b instanceof Double) {
            double ax = ((Double) a);
            double bx = ((Double) b);
            if (operator.equalsIgnoreCase("<")) {
                return ax < bx;
            }
            if (operator.equalsIgnoreCase(">")) {
                return ax > bx;
            }
            if (operator.equalsIgnoreCase("<=")) {
                return ax <= bx;
            }
            if (operator.equalsIgnoreCase(">=")) {
                return ax >= bx;
            }
            if (operator.equalsIgnoreCase("=")) {
                return ax == bx;
            }
            if (operator.equalsIgnoreCase("<>")) {
                return ax != bx;
            }
        } else if (a instanceof String && b instanceof String) {
            String ax = (String) a;
            String bx = (String) b;
            if (operator.equalsIgnoreCase("=")) {
                return ax.equals(bx);
            }
            if (operator.equalsIgnoreCase("<>")) {
                return !ax.equals(bx);
            }
        } else if (a instanceof Boolean && b instanceof Boolean) {
            boolean ax = ((Boolean) a);
            boolean bx = ((Boolean) b);
            if (operator.equalsIgnoreCase("=")) {
                return ax == bx;
            }
            if (operator.equalsIgnoreCase("<>")) {
                return ax != bx;
            }
        }

        return false;
    }

    @Override
    public boolean vsCompareExpression(VSFlowInfo flowInfo, String expr) {
        try {
            bindVars();

            Object o1 = engine.eval(expr, bindings);

            return (boolean) o1;
        } catch (ScriptException ex) {
            Logger.getLogger(Basis.class.getName()).log(Level.SEVERE, null, ex);
        }

        return false;
    }

    public String[] mySplitt(String str) {
        String[] result = new String[3];

        result[0] = "";
        result[1] = "";
        result[2] = "";

        int c = 0;
        String ch;
        boolean oki = false;

        for (int i = 0; i < str.length(); i++) {
            ch = str.substring(i, i + 1);

            if (ch.equalsIgnoreCase("<") || ch.equalsIgnoreCase(">") || ch.equalsIgnoreCase("=")) {
                result[1] += ch;
                oki = true;
            } else {
                if (oki == false) {
                    c = 0;
                }
                if (oki == true) {
                    c = 2;
                }
                result[c] += ch;
            }

        }

        return result;
    }

    public OpenVariable getVariable(String varname) {
        varname = varname.trim();
        OpenVariable node;
        for (int i = 0; i < variablenListe.size(); i++) {
            node = (OpenVariable) variablenListe.get(i);

            if (varname.equals(node.name.trim())) {
                return node;
            }
        }
        return null;
    }

    public OpenVariable getVariable(VSFlowInfo info, String varname) {
        varname = varname.trim();
        OpenVariable node;

        for (int i = 0; i < info.variablenListe.size(); i++) {
            node = (OpenVariable) info.variablenListe.get(i);

            if (varname.equals(node.name.trim())) {
                return node;
            }
        }

        for (int i = 0; i < variablenListe.size(); i++) {
            node = (OpenVariable) variablenListe.get(i);

            if (varname.equals(node.name.trim())) {
                return node;
            }
        }
        return null;
    }

    @Override
    public void vsSetVar(String varname, Object value) {
        OpenVariable o = getVariable(varname);
        if (o != null) {
            if (o.value instanceof Boolean && value instanceof Boolean) {
                o.value = value;
            } else if (o.value instanceof Double && value instanceof Double) {
                o.value = value;
            } else if (o.value instanceof String && value instanceof String) {
                o.value = value;
            } else {
                System.out.println("Error setting Variable");
            }
        } else {
            Tools.showMessage("Variable \"" + varname + "\" not definied! \nPlease define the variable");
        }

        notifyAllElements(varname);

    }

    public void notifyAllElements(String varname) {
        for (int i = 0; i < variableNotifyList.size(); i++) {
            VariableNotifyRecord rec = (VariableNotifyRecord) variableNotifyList.get(i);

            if (rec.varName.equalsIgnoreCase(varname)) {
                rec.element.jProcess();
            }
        }
    }

    public void generateAllVariabled() {
        OpenVariable node;
        for (int i = 0; i < variablenListe.size(); i++) {
            node = (OpenVariable) variablenListe.get(i);

            node.createVariableByDt();
        }
    }

    @Override
    public void vsNotifyMeWhenVariableChanged(ExternalIF element, String varName) {
        variableNotifyList.add(new VariableNotifyRecord(element, varName));
    }

    @Override
    public Stack getStack() {
        return stack;
    }

    @Override
    public Object vsGetVar(VSFlowInfo flowInfo, String varName) {
        Object o = flowInfo.getVariable(varName);

        if (o != null) {
            return o;
        }

        o = vsGetVar(varName);
        if (o != null) {
            return o;
        }

        return null;
    }

    @Override
    public Object vsGetVar(String varname) {
        OpenVariable o = getVariable(varname);

        if (o == null) {
            Tools.showMessage("Variable \"" + varname + "\" not definied! \nPlease define the variable");
        }

        return o.value;
    }

    public FrameMain getFrameCircuit() {
        return frameCircuit;
    }
    public String caption = "";
    public VSImage vsIcon = new VSImage();
    public int fileCount = 0;

    public static final int MODE_NORMAL = 0;
    public static final int MODE_SUBVM = 1;
    public int modus = MODE_NORMAL;

    public PropertyEditor propertyEditor = null;
    public boolean isFileLoaded = false;
    public ArrayList undoHistory = new ArrayList();
    private String elementPath = "";

    public String getElementPath() {
        return elementPath;
    }

    public String fileName = "";
    public String letztesVerzeichniss = "."; // The default save path!
    public int id = -1;
    public String circuitPanelTitel = "";
    public int circuitPanelLeft = 0;
    public int circuitPanelTop = 0;
    public int circuitPanelWidth = 640;
    public int circuitPanelHeight = 480;
    public int circuitBasisWindowWidth = 900;
    public String frontPanelTitel = "";
    public int frontPanelWidth = 300;
    public int frontPanelHeight = 200;
    
    private boolean changed = false;
    private boolean oldRasterOn;
    
    // Die basisVersion ndert sich nur bei nderung der Basis Properties!
    public String basisVersion = "0";
    public ExternalIF ownerElement;
    public Basis ownerBasis = null;
    public String vmFilename = "";
    public boolean canSaveForUndo = true;
    public boolean loading = false;

    public boolean isLoading() {
        return loading;
    }

    public boolean isChanged() {
        return changed;
    }

    public void setChanged(boolean value) {
        changed = value;
    }

    // Begin Falls als Element gesichert
    public int elementWidth = 50;
    public int elementHeight = 50;
    public String elementName = "";
    // Ende

    // Info ueber den Author
    public String basisTitel = "";
    public String basisGrupe = "";
    public String autorName = "";
    public String autorMail = "";
    public String autorWWW = "";
    // Ende Info ueber den Author

    private boolean asSubVMEditor = false;

    public boolean isAsSubVMEditor() {
        return asSubVMEditor;
    }

    public void setAsSubVMEditor(boolean asSubVMEditor) {
        this.asSubVMEditor = asSubVMEditor;
    }

    private boolean editable = true;

    public void setEditable(boolean value) {
        this.editable = value;
    }

    public boolean isEditable() {
        return editable;
    }

    public FrameMain getFrameMain() {
        return frameCircuit;
    }

    public VMObject getCircuitBasis() {
        return circuitBasis;
    }

    public VMObject getFrontBasis() {
        return frontBasis;
    }

    public boolean panelMode = false;

    public boolean getPanelMode() {
        return panelMode;
    }

    public void showCircuitWindow(boolean panelMode) {

    }

    @Override
    public void propertyChanged(Object o) {

    }

    public void showFrontWindow() {
        ownerVMPanel.jTabbedPane1.setSelectedIndex(1);
    }

    public Basis(FrameMain frameCircuit, String elementPath) {
        this.frameCircuit = frameCircuit;
        this.elementPath = elementPath;

        dialogTestpoint = new FrmTestpoints(this);
        frameBooleanGraph = new FrameBooleanGraph(this);
        frameDoubleGraph = new FrameDoubleGraph(this);
        console = new FrameConsoleOutput(this);

        circuitBasis = new VMObject(this, elementPath);
        circuitBasis.setRasterOn(false);
        circuitBasis.setSize(4000, 2000);
        circuitBasis.setPreferredSize(new Dimension(4000, 2000));
        circuitBasis.setBackground(Color.white);

        frontBasis = new VMObject(this, elementPath);
        undoPointer = 0;

        frontBasis.setPreferredSize(new Dimension(500, 500));
        
        WindowsPosition.addItem("CENTER");
        WindowsPosition.addItem("TOP_LEFT");
        WindowsPosition.addItem("TOP_RIGHT");
        WindowsPosition.addItem("BOTTOM_LEFT");
        WindowsPosition.addItem("BOTTOM_RIGHT");
        WindowsPosition.addItem("CUSTOM");
        WindowsPosition.addItem("MAXIMIZED");

        getCircuitBasis().setAlignToGrid(frameCircuit.settings.isCircuittAlignToGrid());
        getCircuitBasis().setRasterOn(frameCircuit.settings.isCircuitRasterOn());
        getCircuitBasis().setRaster(frameCircuit.settings.getCircuitRasterX(), frameCircuit.settings.getCircuitRasterY());
        getFrontBasis().setBackground(Color.WHITE);

        getFrontBasis().setAlignToGrid(frameCircuit.settings.isFrontAlignToGrid());
        getFrontBasis().setRasterOn(frameCircuit.settings.isFrontRasterOn());
        getFrontBasis().setRaster(frameCircuit.settings.getFrontRasterX(), frameCircuit.settings.getFrontRasterY());

        getFrontBasis().setBackground(Color.LIGHT_GRAY);

        initButtonGroups();

        Image img = new javax.swing.ImageIcon(getClass().getResource("/CustomColorPicker/Bild1.gif")).getImage();

        selectionImage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = selectionImage.createGraphics();
        g2.drawImage(img, null, null);
    }

    public void deleteAnythingSelected() {
        getCircuitBasis().deleteAllSelected();
        getFrontBasis().deleteAllSelected();
        frameCircuit.listeAllElements();
    }

    public void println(String message) {
        if (frameCircuit == null) {
            return;
        }

        console.addMessageToConsole(message);
    }

    public void cut() {
        copy();
        deleteAnythingSelected();
    }

    public void copy() {
        try {
            String filename = getFrameMain().getUserURL().getFile() + "/Ablage.tmp";
            saveFile(filename, true);
        } catch (Exception ex) {
            showErrorMessage(ex.toString());
        }
    }

    public void paste() {
        String filename = getFrameMain().getUserURL().getFile() + "/Ablage.tmp";

        if (new File(filename).exists()) {
            loadFile(filename, true);
            getCircuitBasis().ProcessPinDataType();
            setChanged(true);
        }
    }

    public void verknuepfeElemente(boolean fromAblage) {

        if (fromAblage) {
            // 1. suche alle Elemente die mit loadedFromAblageFlag markiert worden sind
            // 2. suche alle circuitElemente und ermittle den passenden panelElement der
            // die panelElementID enthaelt

            for (int i = 0; i < circuitBasis.getElementCount(); i++) {
                Element circuitElement = circuitBasis.getElement(i);

                if (circuitElement.loadedFromAblageFlag == true) {
                    circuitElement.loadedFromAblageFlag = false;
                    for (int j = 0; j < frontBasis.getElementCount(); j++) {
                        Element frontElement = frontBasis.getElement(j);

                        if (frontElement.loadedFromAblageFlag == true) {
                            if (circuitElement.panelElementID == frontElement.oldID) {
                                frontElement.loadedFromAblageFlag = false;
                                circuitElement.panelElementID = frontElement.getID();
                                frontElement.circuitElementID = circuitElement.getID();
                                break;
                            }
                        }
                    }

                }
            }

        }

        for (int i = 0; i < frontBasis.getElementCount(); i++) {
            Element element = frontBasis.getElement(i);
            if (element.circuitElementID != -1) {
                element.circuitElement = (Element) circuitBasis.getObjectWithID(element.circuitElementID);
            }
        }

        for (int i = 0; i < circuitBasis.getElementCount(); i++) {
            Element element = circuitBasis.getElement(i);
            if (element.panelElementID != -1) {
                element.panelElement = (Element) frontBasis.getObjectWithID(element.panelElementID);
            }
        }

    }

    public boolean isWriteable(File f) {
        if (f.exists()) {
            return Tools.setQuestionDialog(frameCircuit, java.util.ResourceBundle.getBundle("VisualLogic/NOI18N/Basic").getString("overrideFile"));
        }
        return true;
    }

    public void saveAs() {
        if (vmProtected) {
            return;
        }

        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(FrameMain.frm.getSavePath().toAbsolutePath().toFile());

        System.out.println(chooser.getCurrentDirectory().toPath().toString());

        chooser.setDialogTitle(java.util.ResourceBundle.getBundle("VisualLogic/NOI18N/Basic").getString("Speichern_als..."));
        chooser.setDialogType(JFileChooser.SAVE_DIALOG);

        VlogicFilter filter = new VlogicFilter();

        chooser.setFileFilter(filter);

        int value = chooser.showSaveDialog(new JFrame());

        if (value == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();

            String fileName = file.getPath();

            String s = file.getName();
            int i = s.lastIndexOf('.');

            if (i > 0) {
            } else {
                fileName += ".vlogic";
            }

            if (isWriteable(new File(fileName))) {
                saveToFile(fileName, false);
                setChanged(false);
            }
        }
    }

    public void save() {
        if (vmProtected) {
            return;
        }
        if (isFileLoaded == true) {
            saveToFile(fileName, false);
        } else {
            saveAs();
        }

        setChanged(false);
    }

    public void deleteHistoryFiles() {
        for (int i = 0; i < undoHistory.size(); i++) {
            String filename = (String) undoHistory.get(i);
            File file = new File(filename);
            file.delete();
        }
    }

    public void close() {
        if (modus != MODE_SUBVM) {
            getCircuitBasis().selectAny(true);

            getCircuitBasis().deleteAllSelected();

            getFrontBasis().selectAny(true);
            getFrontBasis().deleteAllSelected();
        }
    }

    public void closeCircuitWindow() {

    }

    public boolean onClose() {

        if (isChanged()) {

            int result = JOptionPane.showConfirmDialog((Component) null, java.util.ResourceBundle.getBundle("VisualLogic/NOI18N/Basic").getString("Inhalt_Wurde_Geaendert"), java.util.ResourceBundle.getBundle("VisualLogic/NOI18N/Basic").getString("VM"), JOptionPane.YES_NO_CANCEL_OPTION);

            if (result == JOptionPane.YES_OPTION) {
                save();
                return true;
            }

            if (result == JOptionPane.NO_OPTION) {
                return true;
            }
            if (result == JOptionPane.CANCEL_OPTION) {
                return false;
            }
        }
        return true;
    }

    public boolean isRunning() {
        return circuitBasis.isRunning();
    }

    public void stop() {
        debugMode = false;

        started = false;
        circuitBasis.isBasisResizePinVisible = true;
        frontBasis.isBasisResizePinVisible = true;

        dataHistory.clearEntries();

        circuitBasis.stop();
        frontBasis.stop();

        circuitBasis.selectAny(false);
        frontBasis.setLocation(0,0);
        frontBasis.selectAny(false);

        if (ownerVMPanel != null) {
            if (oldPanelFront != null) {
                frontBasis.setRasterOn(oldRasterOn);
            }

            if (frm != null) {
                frm.dispose();
            }
            ownerVMPanel.panelFront.add(getFrontBasis());
            ownerVMPanel.invalidate();

            if (startInFrontMode) {
                System.exit(0);
            }
        }
    }
        
    private void createRunningFrame(boolean unDecoratedIn, boolean AlwaysOnTopIn) {
        this.unDecorated=unDecoratedIn;
        this.AlwaysOnTop=AlwaysOnTopIn;
        
        frm = new FrameRunning(this,unDecorated);
       
        frm.setIconImage(vsIcon.getImage());
        int h = 0;
        if (showToolBar) {
            frm.jToolBar1.setVisible(true);
            frm.jToolBar1.setPreferredSize(new Dimension(10, 33));
            h = frm.jToolBar1.getHeight();
        } else {
            frm.jToolBar1.setVisible(false);
            frm.jToolBar1.setPreferredSize(new Dimension(10, 0));
            h = 0;
        }

        frm.setTitle(caption);
        
        frm.pack();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        int XPosTemp=0;
        int YPosTemp=0;
        if(WindowsPosition.selectedIndex==0){// CENTER
        XPosTemp=screenSize.width / 2 - frm.getWidth() / 2;
        YPosTemp=screenSize.height / 2 - frm.getHeight() / 2;
        }
        if(WindowsPosition.selectedIndex==1){
        XPosTemp=0;
        YPosTemp=0;
        }
        if(WindowsPosition.selectedIndex==2){
        XPosTemp=screenSize.width - frm.getWidth();
        YPosTemp=0;
        }
        if(WindowsPosition.selectedIndex==3){
        XPosTemp=0;
        YPosTemp=screenSize.height - frm.getHeight();
        }
        if(WindowsPosition.selectedIndex==4){
        XPosTemp=screenSize.width - frm.getWidth();
        YPosTemp=screenSize.height - frm.getHeight();
        }
        
        if(WindowsPosition.selectedIndex==5){ // If Customized Position
        //XPosTemp = CustomXwindowPos;
        //YPosTemp = CustomYwindowPos;
        } else {
        CustomXwindowPos = XPosTemp;
        CustomYwindowPos = YPosTemp;    
        }
        
        if(WindowsPosition.selectedIndex==6){// MAXIMIZED 
        frm.setSize(screenSize);
        Rectangle b=this.frontBasis.getBounds();
        try{
        frm.getContentPane().getComponent(1).setBackground(this.frontBasis.getBackground());
        this.frontBasis.setLocation((screenSize.width/2)-(b.width/2),((screenSize.height/2)-(b.height/2)));
        }catch(Exception e){}
        }else{
        frm.setLocation(CustomXwindowPos,CustomYwindowPos);    
        }
        frm.setVisible(true);
        
        if (debugMode || AlwaysOnTop) {
            frm.setAlwaysOnTop(true);
        }
        frm.toFront();
    }

    public void baseprintln(String value) {

        console.setVisible(true);
        console.addMessageToConsole(value + "\n");

    }

    public void start(boolean debugmode) {
        loading = true;

        stack.clear();

        if (frameCircuit != null) {
            if (frameBooleanGraph != null) {
                frameBooleanGraph.init();
            }

            if (frameDoubleGraph != null) {
                frameDoubleGraph.init();
            }

            if (dialogTestpoint != null) {
                dialogTestpoint.init();
            }
        }

        this.debugMode = debugmode;

        circuitBasis.clockList.clear();
        frontBasis.clockList.clear();

        getFrontBasis().sortSubPanels();

        variableNotifyList.clear();
        generateAllVariabled();

        engineManager = new ScriptEngineManager();
        // Javascript engine does not exist in Java 17.
        // Needs rhino and rhino-engine jars for working.
        engine = engineManager.getEngineByName("javascript");
        if (engine == null) {
        	System.err.println("JavaScript Interpreter not found. Terminating!");
        	System.exit(1);
        }

        bindings = engine.getBindings(ScriptContext.ENGINE_SCOPE);

        bindings.put("basis", this);
        engine.setBindings(bindings, ScriptContext.ENGINE_SCOPE);

        OpenVariable node;
        for (int i = 0; i < variablenListe.size(); i++) {
            node = (OpenVariable) variablenListe.get(i);

            bindings.put(node.name, node.value);
        }

        if (circuitBasis.errorExist()) {
            Tools.showMessage(java.util.ResourceBundle.getBundle("VisualLogic/NOI18N/Basic").getString("errors_in_logic_panel"));
        } else {
            circuitBasis.isBasisResizePinVisible = false;
            frontBasis.isBasisResizePinVisible = false;

            circuitBasis.setDraehteInRunMode();
            frontBasis.setDraehteInRunMode();

            started = true;

            circuitBasis.initAllOutputPins();
            frontBasis.initAllOutputPins();

            circuitBasis.initAllInputPins();
            frontBasis.initAllInputPins();

            oldRasterOn = frontBasis.isRasterOn();
            frontBasis.setRasterOn(false);

            if (ownerVMPanel != null && frameCircuit != null && showFrontPanelWhenStart == true) {
                ownerVMPanel.jTabbedPane1.setSelectedIndex(0);

                oldPanelFront = getFrontBasis();

                ownerVMPanel.panelFront.remove(oldPanelFront);
                ownerVMPanel.updateUI();

                createRunningFrame(unDecorated,AlwaysOnTop);
            }

            circuitBasis.start();
            frontBasis.start();

            SwingUtilities.invokeLater(() -> {
                if (frm != null) {
                    frm.toFront();
                }
            });

        }
        loading = false;
    }

    public void pause() {
        circuitBasis.pause();
        frontBasis.pause();
    }

    public void resume() {
        circuitBasis.resume();
        frontBasis.resume();
    }

    public void step() {
        circuitBasis.step();
        frontBasis.step();
    }

    public void disableAllElements() {
        circuitBasis.disableAllElements();
        frontBasis.disableAllElements();
    }

    public void loadFile(String filename, boolean fromAblage) {
        System.out.println(filename);
        loading = true;
        stop();
        disableAllElements();

        this.XfileName = fileName;

        try {

            FileSystemInput fsi = new FileSystemInput(filename);
            if(!filename.endsWith("v")){
            loadFromStream(fsi, fromAblage);
                        } else {
                String line;
                do {
                    line = fsi.readline();
                System.out.println(line); 
                }
                while(line!= null);
                //circuitBasis.loadFromStream(fsIn, fromAblage, ver);
            //frontBasis.loadFromStream(fsIn, fromAblage, ver);

            circuitBasis.verknuepfeDraehte();
            frontBasis.verknuepfeDraehte();
            circuitBasis.reorderWireFrames();
            frontBasis.reorderWireFrames();
            }
            fsi.close();


            circuitBasis.korrigiereFehler();
            frontBasis.korrigiereFehler();

            circuitBasis.reorderWireFrames();
            frontBasis.reorderWireFrames();

            circuitBasis.unlockGraphics();
            frontBasis.unlockGraphics();
        } catch (Exception ex) {
            Logger.getLogger(Basis.class.getName()).log(Level.SEVERE, null, ex);
        }

        circuitBasis.processPropertyEditor();
        frontBasis.sortSubPanels();

        loading = false;
        getCircuitBasis().ProcessPinDataType();
    }

    public void scrambleElementAndWires() {
        VMObject vm = getCircuitBasis();

        Element element;
        for (int i = 0; i < vm.getElementCount(); i++) {
            element = vm.getElement(i);
            element.setLocation(0, 0);
        }

        Draht draht;
        for (int i = 0; i < vm.getDrahtCount(); i++) {
            draht = vm.getDraht(i);
            draht.scrambleAllPoints();
        }

    }

    private String generatePassword() {
        double c = Math.random();
        if (c == 0) {
            c = 1;
        }
        double time = System.currentTimeMillis() / c;

        String result = "" + time;
        return result;
    }

    public void saveAsExecutable(String filename) {
        if (vmProtected) {
            return;
        }

        scrambleElementAndWires();

        String passwd = generatePassword();
        vmPassword = passwd;
        System.out.println("" + vmPassword);
        vmProtected = true;
        saveFile(filename, false);
    }

    public void loadFromFile(String fileName, boolean fromAblage) {

        if (fromAblage == false) {
            this.fileName = fileName;
            clear();
        }

        loadFile(fileName, fromAblage);

        setChanged(false);

        getCircuitBasis().ProcessPinDataType();

    }

    private int undoPointer = 0;

    public int getUndoPointer() {
        return undoPointer;
    }

    public int getUndoHistorySize() {
        return undoHistory.size();
    }

    public void saveForUndoRedo() {
        if (canSaveForUndo) {
            if (undoHistory.size() > undoPointer) {
                while (undoHistory.size() > undoPointer) {
                    undoHistory.remove(undoHistory.size() - 1);
                }
            }

            try {
                File tmp = File.createTempFile("Undo", ".vslogicTemp", null);
                String filename = tmp.getAbsolutePath();
                saveFile(filename, false);
                setChanged(true);
                undoHistory.add(filename);
                undoPointer++;
            } catch (IOException e) {
                Tools.showMessage("Error creating Temp File for Undo/Redo function!");
            }
        }
    }

    public void undo() {
        if (undoPointer > 1) {
            undoPointer--;

            String filename = (String) undoHistory.get(undoPointer - 1);
            clear();

            loadFile(filename, false);
            getCircuitBasis().ProcessPinDataType();
            setChanged(true);
        }
    }

    public void redo() {
        if (undoPointer < undoHistory.size()) {
            undoPointer++;
            String filename = (String) undoHistory.get(undoPointer - 1);
            clear();
            loadFile(filename, false);
            getCircuitBasis().ProcessPinDataType();
            setChanged(true);
        }
    }

    /*
     * Loescht bei CircuitPanel und FrontPanel alle Elemente
     */
    public void clear() {
        while (circuitBasis.getElementCount() > 0) {
            Element element = circuitBasis.getElement(0);
            circuitBasis.deleteElement(element);
        }

        while (frontBasis.getElementCount() > 0) {
            Element element = frontBasis.getElement(0);
            frontBasis.deleteElement(element);
        }

    }

    public void addPublishingFiles(ArrayList list) {
        getCircuitBasis().addPublishingFiles(list);
        getFrontBasis().addPublishingFiles(list);
    }

    /**
     * Konvertiert ein Byte in einen Hex-String.
     */
    public static String toHexString(byte b) {
        int value = (b & 0x7F) + (b < 0 ? 128 : 0);
        String ret = (value < 16 ? "0" : "");
        ret += Integer.toHexString(value).toUpperCase();
        return ret;
    }

    private String bytesToHex(byte[] val) {
        String result = "";
        for (int i = 0; i < val.length; ++i) {
            result += toHexString(val[i]);
        }

        return result;
    }

    private byte[] calcDigest(String str) {
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("SHA");
            md.update(str.getBytes());
            //MessageDigest berechnen und ausgeben
            byte[] result = md.digest();

            return result;
        } catch (NoSuchAlgorithmException ex) {
        }

        return null;
    }

    public void loadFromStream(FileSystemInput fsIn, boolean fromAblage) {
    try {
        fileCount = 0;
        String tmpPassword;
        int modusx = 0;
        String strCaption;
        String strCircuitPanelTitel;
        String strFrontPanelTitel;
        int intElementWidth;
        int intElementHeight;
        String strElementName;
        String strBasisTitel;
        String strBasisVersion;
        String strAutorName;
        String strAutorMail;
        String strAutorWWW;
        
        FileInputStream fis = fsIn.gotoItem(fileCount++);
        DataInputStream stream = new DataInputStream(fis);
        String ver = stream.readUTF(); // Version
        if (Double.parseDouble(ver) == 3.13){
        System.out.println("LoadFomStream_BasisVersion:"+ver+"|");
        tmpPassword = stream.readUTF(); // Password einlesen
                if (tmpPassword.length() > 0) {
                    if (frameCircuit != null && frameCircuit.frontMode == false) {
                        DialogPassword dialog = new DialogPassword(null, true);

                        dialog.setVisible(true);
                        String refPassword = DialogPassword.password;
                        vmPassword = tmpPassword;

                        String res = bytesToHex(calcDigest(refPassword));
                        String res1 = tmpPassword;

                        vmProtected = !res.equals(res1);
                    } else {
                        vmProtected = true;
                    }
                }
                
        modusx = stream.readInt();
        strCaption = stream.readUTF();
        vsIcon.loadFromStream(fis);
        strCircuitPanelTitel = stream.readUTF();
        strFrontPanelTitel = stream.readUTF();
        intElementWidth = stream.readInt();
        intElementHeight = stream.readInt();
        strElementName = stream.readUTF();
        showToolBar = stream.readBoolean();
        unDecorated = stream.readBoolean();
        AlwaysOnTop = stream.readBoolean();
        WindowsPosition.loadFromStream(fis);
        CustomXwindowPos=stream.readInt();
        CustomYwindowPos=stream.readInt();
        strBasisTitel = stream.readUTF();
        strBasisVersion = stream.readUTF();
        strAutorName = stream.readUTF();
        strAutorMail = stream.readUTF();
        strAutorWWW = stream.readUTF();
        if (fromAblage == false) {
            variablenListe.clear();
            int count = stream.readInt();
            for (int i = 0; i < count; i++) {
                OpenVariable node = new OpenVariable();
                node.name = stream.readUTF();
                node.datatype = stream.readInt();
                if (Double.parseDouble(ver) >= 3.84) {
                    System.out.println("Basis.java 1745 Double.parseDouble (ver)" + Double.parseDouble(ver));
                }
                
                    node.size1 = stream.readInt();
                    node.size2 = stream.readInt();

                variablenListe.add(node);
            }
        }

        } else {
            if (Double.parseDouble(ver) < 1.981) {
                showErrorMessage(java.util.ResourceBundle.getBundle("VisualLogic/NOI18N/Basic").getString("Ungueltige_Version_oder_Version_wird_nicht_unterstuetzt"));
                return;
            }

            if (Double.parseDouble(ver) >= 3.12) {
                tmpPassword = stream.readUTF(); // Password einlesen

                if (tmpPassword.length() > 0) {
                    if (frameCircuit != null && frameCircuit.frontMode == false) {
                        DialogPassword dialog = new DialogPassword(null, true);

                        dialog.setVisible(true);
                        String refPassword = DialogPassword.password;
                        vmPassword = tmpPassword;

                        String res = bytesToHex(calcDigest(refPassword));
                        String res1 = tmpPassword;

                        vmProtected = !res.equals(res1);
                    } else {
                        vmProtected = true;
                    }
                }
            }

            if (Double.parseDouble(ver) >= 3.04) {
                modusx = stream.readInt();
            }

            strCaption = stream.readUTF();

            if (Double.parseDouble(ver) >= 3.06) {
                vsIcon.loadFromStream(fis);
            }

            strCircuitPanelTitel = stream.readUTF();
            strFrontPanelTitel = stream.readUTF();
            intElementWidth = stream.readInt();
            intElementHeight = stream.readInt();
            strElementName = stream.readUTF();

            if (Double.parseDouble(ver) >= 3.11) {
                showToolBar = stream.readBoolean();
            }

            strBasisTitel = stream.readUTF();
            strBasisVersion = stream.readUTF();
            strAutorName = stream.readUTF();
            strAutorMail = stream.readUTF();
            strAutorWWW = stream.readUTF();

            if (Double.parseDouble(ver) >= 3.07) {
                if (fromAblage == false) {
                    variablenListe.clear();
                    int count = stream.readInt();

                    for (int i = 0; i < count; i++) {
                        OpenVariable node = new OpenVariable();
                        node.name = stream.readUTF();
                        node.datatype = stream.readInt();
                        if (Double.parseDouble(ver) >= 3.84) {
                            node.size1 = stream.readInt();
                            node.size2 = stream.readInt();
                        }

                        variablenListe.add(node);
                    }
                }
            }
            
        }
        
         if (fromAblage == false) {
                caption = strCaption;
                circuitPanelTitel = strCircuitPanelTitel;
                frontPanelTitel = strFrontPanelTitel;
                elementWidth = intElementWidth;
                elementHeight = intElementHeight;
                elementName = strElementName;
                basisTitel = strBasisTitel;
                basisVersion = strBasisVersion;
                autorName = strAutorName;
                autorMail = strAutorMail;
                autorWWW = strAutorWWW;

                if (basisVersion.equalsIgnoreCase("")) {
                    basisVersion = "0";
                }
            }

            circuitBasis.loadFromStream(fsIn, fromAblage, ver);
            frontBasis.loadFromStream(fsIn, fromAblage, ver);

            circuitBasis.verknuepfeDraehte();
            frontBasis.verknuepfeDraehte();
            circuitBasis.reorderWireFrames();
            frontBasis.reorderWireFrames();
            setChanged(false);

        } catch (IOException | NumberFormatException ex) {
            showErrorMessage(java.util.ResourceBundle.getBundle("VisualLogic/NOI18N/Basic").getString("Fehler_in_Basis.loadFromStream()_:") + ex.toString());
            showErrorMessage("error loading File : " + XfileName + "\n" + ex.getMessage());
        }
        
    }

    public void showErrorMessage(String message) {
        if (frameCircuit != null) {
            frameCircuit.addMessageToConsoleErrorWarnings(message);
        }
    }

    public void ladeNunAlleBasisElemente(VMObject vm, FileSystemInput fsIn) {
        Element element;

        for (int i = 0; i < vm.getElementCount(); i++) {
            element = vm.getElement(i);

            FileInputStream fis = fsIn.gotoItem(fileCount++);
            DataInputStream stream = new DataInputStream(fis);

            try {
                {
                    element.classRef.loadFromStreamAfterXOnInit(fis);
                }
            } catch (Exception | java.lang.AbstractMethodError ex) {
                System.out.println(ex.toString());
            }

            if (element.elementBasis != null) {
                element.elementBasis.getCircuitBasis().processpropertyChangedToAllElements(null);
                element.elementBasis.getFrontBasis().processpropertyChangedToAllElements(null);
            }
        }

    }

    public void setPassword(String pass) {
        String encPassword = bytesToHex(calcDigest(pass));
        vmPassword = encPassword;
    }

    public void saveToStream(FileSystemOutput fsOut, boolean onlySelected) {
      if(Double.parseDouble(Version.strFileVersion)>=3.12){
      try {
            FileOutputStream fos = fsOut.addItem("Properties");
            DataOutputStream dos = new DataOutputStream(fos);
            dos.writeUTF(Version.strFileVersion); // Version
            System.out.println("SavetoStream_BasisVersion:"+Version.strFileVersion+"|");
            if (vmPassword.length() == 0) {
                vmPassword = "";
            }
            dos.writeUTF(vmPassword); // Password
            int modusx = 0;
            dos.writeInt(modusx); // NOPE!
            dos.writeUTF(caption);            // Caption
            vsIcon.saveToStream(fos);
            dos.writeUTF(circuitPanelTitel);
            dos.writeUTF(frontPanelTitel);
            dos.writeInt(elementWidth);
            dos.writeInt(elementHeight);
            dos.writeUTF(elementName);
            dos.writeBoolean(showToolBar);
            dos.writeBoolean(unDecorated);
            dos.writeBoolean(AlwaysOnTop);
            WindowsPosition.saveToStream(fos);
            dos.writeInt(CustomXwindowPos);
            dos.writeInt(CustomYwindowPos);
            dos.writeUTF(basisTitel);
            dos.writeUTF(basisVersion);
            dos.writeUTF(autorName);
            dos.writeUTF(autorMail);
            dos.writeUTF(autorWWW);
            if (onlySelected == false) {
                dos.writeInt(variablenListe.size());
                OpenVariable node;
                for (int i = 0; i < variablenListe.size(); i++) {
                    node = (OpenVariable) variablenListe.get(i);
                    dos.writeUTF(node.name);
                    dos.writeInt(node.datatype);
                    dos.writeInt(node.size1);
                    dos.writeInt(node.size2);
                }
            }
            fsOut.postItem();
            circuitBasis.saveToStream(fsOut, "CircuitPanel", onlySelected);
            frontBasis.saveToStream(fsOut, "FrontPanel", onlySelected);
        } catch (IOException ex) {
            showErrorMessage(java.util.ResourceBundle.getBundle("VisualLogic/NOI18N/Basic").getString("Fehler_in_Basis.saveToStream()_:") + ex.toString());
        }      
      } else {
        try {
            FileOutputStream fos = fsOut.addItem("Properties");
            DataOutputStream dos = new DataOutputStream(fos);

            dos.writeUTF(Version.strFileVersion); // Version
            if (vmPassword.length() == 0) {
                vmPassword = "";
            }

            dos.writeUTF(vmPassword); // Password
            int modusx = 0;
            dos.writeInt(modusx); // NOPE!
            dos.writeUTF(caption);            // Caption
            vsIcon.saveToStream(fos);
            dos.writeUTF(circuitPanelTitel);
            dos.writeUTF(frontPanelTitel);
            dos.writeInt(elementWidth);
            dos.writeInt(elementHeight);
            dos.writeUTF(elementName);
            dos.writeBoolean(showToolBar);

            dos.writeUTF(basisTitel);
            dos.writeUTF(basisVersion);
            dos.writeUTF(autorName);
            dos.writeUTF(autorMail);
            dos.writeUTF(autorWWW);

            if (onlySelected == false) {
                dos.writeInt(variablenListe.size());
                OpenVariable node;
                for (int i = 0; i < variablenListe.size(); i++) {
                    node = (OpenVariable) variablenListe.get(i);
                    dos.writeUTF(node.name);
                    dos.writeInt(node.datatype);
                    dos.writeInt(node.size1);
                    dos.writeInt(node.size2);
                }
            }

            fsOut.postItem();

            circuitBasis.saveToStream(fsOut, "CircuitPanel", onlySelected);
            frontBasis.saveToStream(fsOut, "FrontPanel", onlySelected);
        } catch (IOException ex) {
            showErrorMessage(java.util.ResourceBundle.getBundle("VisualLogic/NOI18N/Basic").getString("Fehler_in_Basis.saveToStream()_:") + ex.toString());
        }
      }

    }

    private void saveX(Element element, FileSystemOutput fsOut) {
        FileOutputStream fos = fsOut.addItem("Basis-Element-Data");
        DataOutputStream dos = new DataOutputStream(fos);

        try {
            element.classRef.saveToStreamAfterXOnInit(fos);
        } catch (Exception | java.lang.AbstractMethodError ex) {
            System.out.println(ex);
        }
    }

    public void speichereNunAlleBasisElemente(VMObject vm, FileSystemOutput fsOut, boolean onlySelected) {

        Element element;
        for (int i = 0; i < vm.getElementCount(); i++) {
            element = vm.getElement(i);

            if (onlySelected) {
                if (element.isSelected()) {
                    saveX(element, fsOut);
                }
            } else {
                saveX(element, fsOut);
            }

        }
    }

    private void saveFile(String fileName, boolean onlySelected) {
        try {
            FileSystemOutput fsOut = new FileSystemOutput(fileName);
            saveToStream(fsOut, onlySelected);
            fsOut.close();
        } catch (Exception ex) {
            showErrorMessage(ex.toString());
        }
    }

    public void saveToFile(String fileName, boolean onlySelected) {
        this.fileName = fileName;
        vmFilename = fileName;
        isFileLoaded = true;
        setChanged(false);

        if (ownerVMPanel != null && frameCircuit != null && panelLabel != null) {
            int index = frameCircuit.getTabIndex(ownerVMPanel);

            if (index > -1) {
                panelLabel.setText(new File(fileName).getName());
            }
        }
        saveFile(fileName, onlySelected);
    }

    public ExternalIF panelElement;
    public ExternalIF element;

    @Override
    public void setPropertyEditor() {

    }

    @Override
    public void xpaint(java.awt.Graphics g) {

    }

    @Override
    public void xsetExternalIF(ExternalIF externalIF) {

    }

    public void jSetChanged(boolean value) {
        changed = value;
    }

    @Override
    public void changePin(int pinIndex, Object value) {

    }

    @Override
    public void resetValues() {

    }

    @Override
    public void xonChangeElement() {

    }

    @Override
    public void xonMouseDragged(MouseEvent e) {

    }

    @Override
    public void xonMousePressed(MouseEvent e) {
        getCircuitBasis().mousePressed(e);
    }

    @Override
    public void xonMouseReleased(MouseEvent e) {
    }

    @Override
    public void xonMouseMoved(MouseEvent e) {

    }

    @Override
    public void xonInitInputPins() {
        getCircuitBasis().xonInitInputPins();
        getFrontBasis().xonInitInputPins();
    }

    public void xonInitOutputPins() {
        getCircuitBasis().initAllOutputPins();
        getFrontBasis().initAllOutputPins();
        getCircuitBasis().xonInitOutputPins();
        getFrontBasis().xonInitOutputPins();
    }

    @Override
    public void checkPinDataType() {

    }

    @Override
    public void xonStart() {
        getCircuitBasis().xonStart();
        getFrontBasis().xonStart();
    }

    @Override
    public void xonStop() {
        getCircuitBasis().xonStop();
        getFrontBasis().xonStop();
    }

    @Override
    public void xonProcess() {
        getCircuitBasis().xonProcess();
        getFrontBasis().xonProcess();
    }

    @Override
    public void xopenPropertyDialog() {
        showCircuitWindow(false);
    }

    @Override
    public void onDispose() {
        try {// Added on v3.12.0
            this.finalize();
        } catch (Throwable ex) {
            Logger.getLogger(Basis.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void xOnInit() {
    }

    @Override
    public void saveToStreamAfterXOnInit(FileOutputStream fos) {
    }

    @Override
    public void loadFromStreamAfterXOnInit(FileInputStream fis) {
    }

    @Override
    public void xSaveToStream(java.io.FileOutputStream fos) {
    }

    @Override
    public void xLoadFromStream(java.io.FileInputStream fis) {
    }

    @Override
    public String xgetName() {
        return "";
    }

    @Override
    public void vsPaint(Graphics g, int x, int y) {
        getFrontBasis().paint(g);
    }

    @Override
    public int getPinsLeftCount() {
        VMObject vm = getCircuitBasis();
        int c = 0;
        for (int i = 0; i < vm.getComponentCount(); i++) {
            Component comp = vm.getComponent(i);
            if (comp instanceof Element) {
                Element element = (Element) comp;
                String str = element.getName();
                if (str.equalsIgnoreCase("#PININPUT_INTERNAL#")) {
                    c++;
                }
            }
        }
        return c;
    }

    @Override
    public int getPinsRightCount() {
        VMObject vm = getCircuitBasis();
        int c = 0;
        for (int i = 0; i < vm.getComponentCount(); i++) {
            Component comp = vm.getComponent(i);
            if (comp instanceof Element) {
                Element element = (Element) comp;
                String str = element.getName();
                if (str.equalsIgnoreCase("#PINOUTPUT_INTERNAL#")) {
                    c++;
                }
            }
        }
        return c;

    }

    @Override
    public int vsGetFrontVMPanelWidth() {
        return getFrontBasis().getWidth();
    }

    @Override
    public int vsGetFrontVMPanelHeight() {
        return getFrontBasis().getHeight();
    }

    @Override
    public void vsSetBackgroungColor(Color color) {
    }

    // Alle Subroutinen werden vor dem Start
    // in einer Liste gespeichert!
    // vsStartSubRoutine startet die Routine aus dieser Liste!
    // Die Liste wird in der VM gehalten
    @Override
    public void vsStartSubRoutine(ExternalIF sourceElement, String name, ArrayList paramList) {
        name = name.trim();
        ExternalIF[] elements = circuitBasis.getElementList(this, "#FLOWCHART_START#");

        Bindings b = engine.createBindings();
        b.put("basis", this);

        Object o;
        Object o2;
        for (ExternalIF element1 : elements) {
            Element elementX = (Element) element1;
            o = elementX.jGetTag(0);
            if (o instanceof String) {
                String oo = ((String) o).trim();
                if (oo.equalsIgnoreCase(name)) {
                    o2 = elementX.jGetTag(1);

                    VSFlowInfo flowInfo = (VSFlowInfo) o2;
                    flowInfo.bindings = b;
                    flowInfo.variablenListe.clear();

                    if (flowInfo.parameterDefinitions.size() == paramList.size()) {
                        for (int j = 0; j < flowInfo.parameterDefinitions.size(); j++) {
                            OpenVariable var = (OpenVariable) flowInfo.parameterDefinitions.get(j);

                            Object obj = paramList.get(j);

                            if (var.datatype == flowInfo.getDataType(obj)) {
                                flowInfo.addVariable(var.name, var.datatype);
                                flowInfo.setVariable(var.name, obj);

                                var.createVariableByDt();
                                
                                var.value=obj;
                                
                                flowInfo.bindings.put(var.name, var.value);
                                
                            }
                        }

                    } else {
                        elementX.jException("parameter count have to be equal!");
                        return;
                    }

                    if (elementX != null && elementX.classRef != null) {
                        flowInfo.source = sourceElement;
                        try {
                            elementX.classRef.processMethod(flowInfo);
                        } catch (Exception ex) {
                            System.out.println("" + ex);
                        }
                    }
                }

            }
        }
    }

    @Override
    public void vsStartElement(int elementID) {
        try {
            Element element = getCircuitBasis().getElementWithID(elementID);

            element.classRef.xonProcess();
        } catch (Exception ex) {
            System.out.println("" + ex);
        }
    }

    @Override
    public Object vsEvaluate(VSFlowInfo flowInfo, String expression) {
        try {
            OpenVariable node;
            Object o1;

            Bindings b=bindings;
            ArrayList vars=variablenListe;

            if (flowInfo.bindings != null) {
                b = flowInfo.bindings;
                vars= flowInfo.variablenListe;
            }

            o1 = engine.eval(expression, b);

            for (int i = 0; i < vars.size(); i++) {
                node = (OpenVariable) vars.get(i);
                
                if (b.get(node.name) instanceof Double && node.value instanceof Integer){
                    node.value = ((Double)b.get(node.name)).intValue();
                }else
                if (b.get(node.name) instanceof Integer && node.value instanceof Double){
                    node.value = ((Integer)b.get(node.name)).doubleValue();
                }else {
                    node.value = b.get(node.name);
                }   

                notifyAllElements(node.name);
            }

            return o1;
        } catch (ScriptException ex) {
            Logger.getLogger(Basis.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "";
    }

    @Override
    public void vsReturnSubProcedure(VSFlowInfo flowInfo) {
        // Suche das letzte auf dem Stack mit den Namen!
        Element el = ((Element) flowInfo.source);

        if (el != null && el.classRef != null) {
            el.classRef.returnFromMethod(flowInfo.returnValue);
        }
    }

    @Override
    public void vsShow() {
        if (ownerBasis != null) {
            if (ownerBasis.frameCircuit != null) {
                if (ownerBasis.frameCircuit.isBasisInDesktop(fileName) == null) {
                    modus = MODE_SUBVM;

                    this.frameCircuit = ownerBasis.frameCircuit;
                    ownerBasis.frameCircuit.addBasisToVMPanel(this);
                    // Transparenz aufheben!
                    getCircuitBasis().setOpaque(true);
                    getFrontBasis().setOpaque(true);

                    isFileLoaded = true;

                    setEditable(true);
                    stop();

                    loading = false;
                } else if (ownerBasis != null && ownerBasis.frameCircuit != null) {
                    ownerBasis.frameCircuit.jPaneVMPanels.setSelectedComponent(this.ownerVMPanel);
                }
            }
        }
    }

    @Override
    public void vsShowFrontPanelWhenStart(boolean value) {
        showFrontPanelWhenStart = value;
    }

    @Override
    public String vsGetProjectPath() {
        return new File(projectPath).getPath();
    }

    public void vsXLoadFromFile(String fileName) {
        fileName = Tools.mapFile(fileName);

        loadFromFile(fileName, false);

        circuitBasis.ProcessDeGruppierer();
        frontBasis.ProcessDeGruppierer();
        circuitBasis.ProcessDeGruppierer();
        frontBasis.ProcessDeGruppierer();
    }

    @Override
    public void vsLoadFromFile(String fileName) {
        vsXLoadFromFile(fileName);
    }

    @Override
    public void vsClose() {
        close();
    }

    @Override
    public void vsStart() {
        start(debugMode);
    }

    @Override
    public void vsStop() {
        stop();
    }

    @Override
    public ExternalIF vsgetOwnerElement() {
        return ownerElement;
    }

    @Override
    public void vsProcess() {
        getCircuitBasis().processAllElements();
    }

    @Override
    public void vsMousePressed(MouseEvent e) {
        getFrontBasis().mousePressed(e);
    }

    @Override
    public void vsMouseReleased(MouseEvent e) {
        getFrontBasis().mouseReleased(e);
    }

    @Override
    public void vsMouseMoved(MouseEvent e) {

    }

    @Override
    public void vsMouseDragged(MouseEvent e) {

    }

    @Override
    public void vsloadFromXML(org.w3c.dom.Element nodeElement) {
    }

    @Override
    public void vssaveToXML(org.w3c.dom.Element nodeElement) {
    }

    @Override
    public void vsLoadFromStream(FileInputStream fis) {
    }

    @Override
    public void vsSaveToStream(FileOutputStream fos) {
    }

    @Override
    public void beforeInit(String[] args) {
    }

    @Override
    public int vsGetCircuitPanelComponentCount() {
        return getCircuitBasis().getComponentCount();
    }

    @Override
    public int vsGetFrontPanelComponentCount() {
        return getFrontBasis().getComponentCount();
    }

    @Override
    public String getBasisElementVersion() {
        return basisVersion;
    }

    @Override
    public String getBinDir() {
        return "";
    }

    @Override
    public void elementActionPerformed(ElementActionEvent evt) {
    }

    @Override
    public void destElementCalled() {
    }

    @Override
    public String jGetVMFilename() {
        return "";
    }

    @Override
    public void xonMousePressedOnIdle(MouseEvent e) {
    }

    @Override
    public void xonClock() {
        getCircuitBasis().processClock();
    }

    @Override
    public void processMethod(VSFlowInfo flowInfo) {
    }

    /**
     *
     * @param result
     */
    @Override
    public void returnFromMethod(Object result) {
    }

    @Override
    public String getJavascriptEditor() {
        return getFrameMain().settings.getJavascript_editor();
    }

    public void setProperexterntyEditor() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    private void bindVars() {
        OpenVariable node;
        for (int i = 0; i < variablenListe.size(); i++) {
            node = (OpenVariable) variablenListe.get(i);

            bindings.put(node.name, node.value);

        }
    }

    @Override
    public ScriptEngine vsGetEngine() {
        return engine;
    }
}
