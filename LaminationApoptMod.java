import java.awt.Color;

import utils.MyGradient;
import ini.cx3d.biology.Cell;
import ini.cx3d.biology.CellElement;
import ini.cx3d.biology.LocalBiologyModule;
import ini.cx3d.biology.NeuriteElement;
import ini.cx3d.biology.SomaElement;
import ini.cx3d.physics.IntracellularSubstance;
import ini.cx3d.physics.PhysicalNode;
import ini.cx3d.physics.PhysicalSphere;
import ini.cx3d.simulation.ECM;
import ini.cx3d.utilities.Matrix;


public class LaminationApoptMod implements LocalBiologyModule {

	private CellElement ce = null;


	@Override
	public void run() {

		PhysicalSphere ps = ce.getPhysical().getAsPhysicalSphere();
		double apoptosisConc = ps.getExtracellularConcentration("apoptosisSub");

		if (apoptosisConc>0.1) {

			int MZNeighbors = 0;
			int l6Neighbors = 0;
			int l5Neighbors = 0;
			int l4Neighbors = 0;
			int l3Neighbors = 0;
			int l2Neighbors = 0;

			for (PhysicalNode pn : ps.getNeighboringPhysicalNodes()) {
				if (pn.isAPhysicalSphere()) {
					PhysicalSphere currPS = pn.getAsPhysicalSphere();
					if (Matrix.distance(ps.massLocation, currPS.massLocation) < 20.0) {

						String nbType = currPS.getSomaElement().getPropertiy("type");
						if (nbType.startsWith("layer6")) {
							l6Neighbors++;
						}
						if (nbType.startsWith("layer5")) {
							l5Neighbors++;
						}
						if (nbType.startsWith("layer4")) {
							l4Neighbors++;
						}
						if (nbType.startsWith("layer3")) {
							l3Neighbors++;
						}
						if (nbType.startsWith("layer2")) {
							l2Neighbors++;
						}
						if (nbType.equalsIgnoreCase("MZ")) {
							MZNeighbors++;
						}
					}
				}
			}

			if (ce.getPropertiy("type").equalsIgnoreCase("MZ")) {

				if (((l6Neighbors+l5Neighbors+l4Neighbors+l3Neighbors)>0)||(l2Neighbors>2)||(Math.random()<0.9)||(MZNeighbors<1)) {	
					ce.getPhysical().getAsPhysicalSphere().modifyExtracellularQuantity("apoptosisSub", 9999999);
					ps.setDiameter(0.01);
					ps.setColor(Color.GREEN);
					ECM.getInstance().removeFromSimulation(ps.getSomaElement());
					ECM.getInstance().removePhysicalSphere(ps);
					ps.removeLocally();
					Lamination.apoptosisMZCounter++;
					ce.removeLocally();
					ce.removeLocalBiologyModule(this);

				}
				else {
					ce.removeLocalBiologyModule(this);
				}
				
			}

			if (ce.getPropertiy("type").equalsIgnoreCase("layer2")) {	
				if ((l3Neighbors>2)||(MZNeighbors>2)||(l6Neighbors+l5Neighbors+l4Neighbors>0)) {
					ce.getPhysical().getAsPhysicalSphere().modifyExtracellularQuantity("apoptosisSub", 9999999);
					ps.setDiameter(0.01);
					Lamination.apoptosisL2Counter++;
					ps.setColor(Color.GREEN);
					ECM.getInstance().removeFromSimulation(ps.getSomaElement());
					ECM.getInstance().removePhysicalSphere(ps);
					ps.removeLocally();
					ce.removeLocally();
					ce.removeLocalBiologyModule(this);
				}
				else {
					ce.removeLocalBiologyModule(this);
				}
			}

			if (ce.getPropertiy("type").equalsIgnoreCase("layer3")) {	

				if ((l4Neighbors>2)||(l2Neighbors>2)||(l6Neighbors+l5Neighbors>0)) {
					ce.getPhysical().getAsPhysicalSphere().modifyExtracellularQuantity("apoptosisSub", 9999999);
					ps.setDiameter(0.01);
					ps.setColor(Color.GREEN);
					ECM.getInstance().removeFromSimulation(ps.getSomaElement());
					ECM.getInstance().removePhysicalSphere(ps);
					ps.removeLocally();
					Lamination.apoptosisL3Counter++;
					ce.removeLocally();
					ce.removeLocalBiologyModule(this);
				}
				else {
					ce.removeLocalBiologyModule(this);
				}
			}

			if (ce.getPropertiy("type").equalsIgnoreCase("layer4")) {

				if ((l5Neighbors>2)||(l3Neighbors>2)||(l6Neighbors>0)) {
					ce.getPhysical().getAsPhysicalSphere().modifyExtracellularQuantity("apoptosisSub", 9999999);
					ps.setDiameter(0.01);
					ps.setColor(Color.GREEN);
					ECM.getInstance().removeFromSimulation(ps.getSomaElement());
					ECM.getInstance().removePhysicalSphere(ps);
					ps.removeLocally();
					Lamination.apoptosisL4Counter++;
					ce.removeLocally();
					ce.removeLocalBiologyModule(this);
				}
				else {
					ce.removeLocalBiologyModule(this);
				}
			}

			if (ce.getPropertiy("type").equalsIgnoreCase("layer5")) {	

				if ((l6Neighbors>2)||(l4Neighbors>2)) {	
					ce.getPhysical().getAsPhysicalSphere().modifyExtracellularQuantity("apoptosisSub", 9999999);
					ps.setDiameter(0.01);
					ps.setColor(Color.GREEN);
					ECM.getInstance().removeFromSimulation(ps.getSomaElement());
					ECM.getInstance().removePhysicalSphere(ps);
					ps.removeLocally();
					Lamination.apoptosisL5Counter++;
					ce.removeLocally();
					ce.removeLocalBiologyModule(this);
				}
				else {
					ce.removeLocalBiologyModule(this);
				}
			}

			if (ce.getPropertiy("type").startsWith("layer6")) {
				if ((l5Neighbors>2)||(l4Neighbors+l3Neighbors+l2Neighbors+MZNeighbors>0)) {	
					ce.getPhysical().getAsPhysicalSphere().modifyExtracellularQuantity("apoptosisSub", 9999999);
					ps.setDiameter(0.01);
					ps.setColor(Color.GREEN);
					ECM.getInstance().removeFromSimulation(ps.getSomaElement());
					ECM.getInstance().removePhysicalSphere(ps);
					ps.removeLocally();
					Lamination.apoptosisL6Counter++;
					ce.removeLocally();
					ce.removeLocalBiologyModule(this);
				}
				else {
					ce.removeLocalBiologyModule(this);
				}
			}

		}
	}

	public LaminationApoptMod() {
	}

	@Override
	public void setCellElement(CellElement cellElement) {
		// TODO Auto-generated method stub
		this.ce = cellElement;
	}



	@Override
	public LocalBiologyModule getCopy() {
		// TODO Auto-generated method stub
		return this.getCopy();
	}

	@Override
	public boolean isCopiedWhenNeuriteBranches() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isCopiedWhenSomaDivides() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isCopiedWhenNeuriteElongates() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isCopiedWhenNeuriteExtendsFromSoma() {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public boolean isDeletedAfterNeuriteHasBifurcated() {
		// TODO Auto-generated method stub
		return false;
	}

	public void setType(String type) {
	}


	public void initGRN() {


	}


	private void runCellCycleDiffStep() {

	}
}
