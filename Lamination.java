import ini.cx3d.Param;
import ini.cx3d.biology.Cell;
import ini.cx3d.biology.CellFactory;
import ini.cx3d.gui.Canvas3d;
import ini.cx3d.gui.physics.SphereSliceDrawer;
import ini.cx3d.parallelization.ObjectHandler.ThreadHandler;
import ini.cx3d.parallelization.ObjectHandler.commands.AbstractSimpleCommand;
import ini.cx3d.parallelization.communication.Hosts;
import ini.cx3d.parallelization.communication.Server;
import ini.cx3d.physics.IntracellularSubstance;
import ini.cx3d.physics.Substance;
import ini.cx3d.simulation.ECM;
import ini.cx3d.simulation.MultiThreadScheduler;
import ini.cx3d.simulation.SimulationState;
import ini.cx3d.spacialOrganisation.ManagerResolver;
import ini.cx3d.spacialOrganisation.PartitionManager;
import ini.cx3d.utilities.Cuboid;
import ini.cx3d.utilities.InputParser;
import ini.cx3d.utilities.export.rendering.ImageExport;
import ini.cx3d.utilities.export.rendering.MonitoringImage;

import java.awt.Color;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import utils.MyGradient;

public class Lamination {
	//public static int proc = 2;

	// simulation boundaries.
	public static double [] lc = new  double[]{-153,-5,-153};
	public static double [] oc = new  double[]{153,1400,153};

	// physical boundaries
	public static double [] cxlc = new  double[]{lc[0]+5,lc[1]+5,lc[2]+5};
	public static double [] cxoc = new  double[]{oc[0]-5,oc[1]-5,oc[2]-5};

	// visualisation boundaries
	public static double [] cxlc2 = new  double[]{lc[0],lc[1]-150,lc[2]+10};
	public static double [] cxoc2 = new  double[]{oc[0],oc[1]-100,oc[2]-10};

	// biological boundaries
	public static double [] smallCx = new  double[]{cxlc[0]+3,cxlc[1],cxlc[2]+3};
	public static double [] bigCx = new  double[]{cxoc[0]-3,cxoc[1]-3,cxoc[2]-3};

	public static Canvas3d c1;
	public static Canvas3d c2;
	
	public static int apoptosisL6Counter = 0;
	public static int apoptosisL5Counter = 0;
	public static int apoptosisL4Counter = 0;
	public static int apoptosisL3Counter = 0;
	public static int apoptosisL2Counter = 0;
	public static int apoptosisMZCounter = 0;
	
	public static int apoptosisA1Counter = 0;
	
	public static int apoptosisA1L6Counter = 0;
	public static int apoptosisA1L5Counter = 0;
	public static int apoptosisA1L4Counter = 0;
	public static int apoptosisA1L3Counter = 0;
	public static int apoptosisA1L2Counter = 0;
	public static int apoptosisA1L1Counter = 0;

	public static void main(String[] args) throws IOException {


		InputParser.fillStandard();
		InputParser.interpeteArguments(args);

		Hosts.getHosts();
		new Thread(Server.getServer()).start();
		ThreadHandler.init();
		Param.NEURITE_MAX_LENGTH = 20;

		MultiThreadScheduler.printCurrentECMTime = true;
		MultiThreadScheduler.runExtracellularDiffusion = true;
		MultiThreadScheduler.runPhyics = true;
		MultiThreadScheduler.runIntracelularDiffusion= false;

		// Initialise substances
		ECM.getInstance().addNewIntracellularSubstanceTemplate(new IntracellularSubstance("counterSub", 0, 0.1, 1, 1));
		ECM.getInstance().addNewSubstanceTemplate(new Substance("apoptosisSub", 100, 0.01));

		double[] point1 = new double[]{0,0,0};
		double[] point2 = new double[]{0,1000,0};

		new MyGradient("GradY", Color.BLUE, point1, point2, 10, 1);		

		Cuboid cub = new Cuboid(cxlc2,cxoc2);
		MonitoringImage m = getImageGen(cub);		
		m.setViewingSize720p();
		m.component.setRoi(cub);
		c1 = m.component;
		c1.optimizeViewToRoi();

		ImageExport exporter = new ImageExport("Lamination", m);
		SimulationState.getLocal().exporters.add( exporter);
		exporter.processinground = 401;


		ManagerResolver.I().createInitialPartitionManager(lc, oc);

		for(int i = 0;i<2;i++)
		{
			for(PartitionManager p2 : ManagerResolver.I().getLocalPartitions())
			{
				p2.split();
			}
		}

		ECM.setRandomSeedTo(1L); 
		ECM.getInstance().setBoundaries(cxlc[0],cxoc[0] , cxlc[1], cxoc[1] , cxlc[2], cxoc[2]);
		ECM.getInstance().setArtificialWallsForCylinders(true);

		new MySetSaveRound(99999999).apply();
		
		if(ECM.toBeLoaded() ==null)
		{	
			simulateNeuralPlateFormation();
		}

		System.out.println("simulating one step");
		MultiThreadScheduler.simulateOneStep();

		initLayerCellNrInfo();
		
		int layerCellNrCounter = 0;

		for (int counter = 0; counter < 60002; counter++) 

		{
			
			if (counter%10==0) {
				layerCellNrCounter++;
				exportLayerInfo(layerCellNrCounter);
			}

			MultiThreadScheduler.simulateOneStep();
		}

		exportParameters();
		System.out.println("Simulation finished");
		System.exit(0);
	}

	public static void simulateNeuralPlateFormation(){
		double [] cxlc = smallCx;
		double [] cxoc = bigCx;
		double y = cxlc[1]+20;
		int nx =(int)((cxoc[0]-10) - (cxlc[0]+10))/10-1;
		int ny = (int)((cxoc[2]-10) - (cxlc[2]+10))/10-1;
		ArrayList<Cell> cells = CellFactory.get2DCellGrid(cxlc[0]+10, cxoc[0]-10,cxlc[2]+10 , cxoc[2]-10,y ,nx ,ny , 0.3);
		System.out.println("Neuraplate size:"+cells.size());
		for (Cell c : cells) {
			c.getSomaElement().getPhysicalSphere().setInterObjectForceCoefficient(1);
			c.getSomaElement().getPhysicalSphere().getSomaElement().getPhysicalSphere().setMass(1);
			c.getSomaElement().getPhysicalSphere().getSomaElement().getPhysicalSphere().setDiameter(10);
		}
		int temp =  SimulationState.getLocal().balanceRound;
		SimulationState.getLocal().balanceRound = 9999999;
		for(int i=0;i<300;i++)
		{
			MultiThreadScheduler.simulateOneStep();
		}
		
		for (Cell c : cells) {
			c.getSomaElement().getPhysicalSphere().setMass(1000.0);
			IntracellularSubstance counterSub = new IntracellularSubstance("counterSub",0,0.1, 1, 1);
			c.getSomaElement().getPhysicalSphere().addIntracellularSubstance(counterSub);
			LaminationSymMod m = new LaminationSymMod();
			m.setCounterSubQuant(100);
			m.setCellElement(c.getSomaElement());

			c.getSomaElement().getPhysicalSphere().setColor(Color.GREEN);
			c.getSomaElement().addLocalBiologyModule(m);
		}
		SimulationState.getLocal().balanceRound = temp;

	}
	
	public static MonitoringImage getImageGen(Cuboid thisROI)
	{
		MonitoringImage t = new MonitoringImage();
		SphereSliceDrawer slice = new SphereSliceDrawer();
		slice.setThickness(5000);
		t.addDrawer(slice, true);
		return t;
	}

	private static void initLayerCellNrInfo() throws IOException {
		BufferedWriter output = null;
		try {
			File file = new File("layerCellNrInfoLam1.m");
			output = new BufferedWriter(new FileWriter(file));
			output.write("L = zeros(22,6000);\n");

		} catch ( IOException e ) {
			e.printStackTrace();
		} finally {
			if (output != null) {
				output.close();
			}
		}
	}

	private static void exportLayerInfo(int timeStep) throws IOException {
		ECM ecm = ECM.getInstance();

		int nrLayer6 = 0;
		int nrLayer5 = 0;
		int nrLayer4 = 0;
		int nrLayer3 = 0;
		int nrLayer2 = 0;
		int nrLayer1 = 0;
		BufferedWriter output = null;

		try {
			output = new BufferedWriter(new FileWriter(new File("layerCellNrInfoLam1.m"),true));
			String currCellType;
			for (Cell ce : ecm.getCellList()) {
				currCellType = ce.getSomaElement().getPropertiy("type");	
				if (currCellType.startsWith("layer6_stop")) {
					nrLayer6++;
				}
				if (currCellType.startsWith("layer5")) {
					nrLayer5++;
				}
				if (currCellType.startsWith("layer4")) {
					nrLayer4++;
				}
				if (currCellType.startsWith("layer3")) {
					nrLayer3++;
				}
				if (currCellType.startsWith("layer2")) {
					nrLayer2++;
				}
				if (currCellType.startsWith("MZ")) {
					nrLayer1++;
				}

			}

			output.append("L(1," + timeStep + ") = " + nrLayer1 + ";\n");
			output.append("L(2," + timeStep + ") = " + nrLayer2 + ";\n");
			output.append("L(3," + timeStep + ") = " + nrLayer3 + ";\n");
			output.append("L(4," + timeStep + ") = " + nrLayer4 + ";\n");
			output.append("L(5," + timeStep + ") = " + nrLayer5 + ";\n");
			output.append("L(6," + timeStep + ") = " + nrLayer6 + ";\n");
			output.append("L(7," + timeStep + ") = " + ecm.getCellList().size() + ";\n");
			
			int apoptosisA2Counter = apoptosisMZCounter+apoptosisL2Counter+apoptosisL3Counter+apoptosisL4Counter+apoptosisL5Counter+apoptosisL6Counter;
			output.append("L(8," + timeStep + ") = " + apoptosisMZCounter + ";\n");
			output.append("L(9," + timeStep + ") = " + apoptosisL2Counter + ";\n");
			output.append("L(10," + timeStep + ") = " + apoptosisL3Counter + ";\n");
			output.append("L(11," + timeStep + ") = " + apoptosisL4Counter + ";\n");
			output.append("L(12," + timeStep + ") = " + apoptosisL5Counter + ";\n");
			output.append("L(13," + timeStep + ") = " + apoptosisL6Counter + ";\n");
			
			output.append("L(14," + timeStep + ") = " + apoptosisA1Counter + ";\n");
			output.append("L(15," + timeStep + ") = " + apoptosisA2Counter + ";\n");
			
			output.append("L(16," + timeStep + ") = " + apoptosisA1L1Counter + ";\n");
			output.append("L(17," + timeStep + ") = " + apoptosisA1L2Counter + ";\n");
			output.append("L(18," + timeStep + ") = " + apoptosisA1L3Counter + ";\n");
			output.append("L(19," + timeStep + ") = " + apoptosisA1L4Counter + ";\n");
			output.append("L(20," + timeStep + ") = " + apoptosisA1L5Counter + ";\n");
			output.append("L(21," + timeStep + ") = " + apoptosisA1L6Counter + ";\n");
			
			int apoptosisA1Counter = apoptosisA1L1Counter+apoptosisA1L2Counter+apoptosisA1L3Counter+apoptosisA1L4Counter+apoptosisA1L5Counter+apoptosisA1L6Counter;
			output.append("L(22," + timeStep + ") = " + apoptosisA1Counter + ";\n");
			
			
		} catch ( IOException e ) {
			e.printStackTrace();
		} finally {
			if (output != null) {
				output.close();
			}
		}
		
	}


private static void exportParameters() throws IOException {

	BufferedWriter output = null;

	try {

		File file = new File("simParams"+".txt");
		output = new BufferedWriter(new FileWriter(file));
		output.write(LaminationNeuronMod.prolifMZcommit+" / "+LaminationDiffMod.layer6prog_commit + " / "+LaminationDiffMod.layer5prog_commit + " / "+LaminationDiffMod.layer4prog_commit + " / "+LaminationDiffMod.layer3prog_commit + " / "+ LaminationDiffMod.layer2prog_commit +";\n");
		output.write(LaminationDiffMod.prolif0commit + " / "+LaminationDiffMod.prolif1commit + " / "+LaminationDiffMod.prolif2commit + " / "+ LaminationDiffMod.prolif3commit + " / "+ LaminationDiffMod.prolif4commit +";\n");

	} catch ( IOException e ) {
		e.printStackTrace();
	} finally {
		if (output != null) {
			output.close();
		}
	}
	
}

}

class MySetSaveRound extends AbstractSimpleCommand<Boolean>{
	int saveround;

	public MySetSaveRound(int i)
	{
		saveround =i;
	}

	@Override
	public boolean apply() {
		if(Hosts.getPrevHost() !=null)
		{
			this.remoteExecute(Hosts.getPrevHost());
		}
		SimulationState.getLocal().saveround = saveround;
		return false;
	}

}


