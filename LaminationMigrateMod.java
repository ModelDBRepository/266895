import java.awt.Color;

import utils.MyGradient;
import ini.cx3d.biology.CellElement;
import ini.cx3d.biology.LocalBiologyModule;
import ini.cx3d.biology.NeuriteElement;
import ini.cx3d.biology.SomaElement;
import ini.cx3d.physics.PhysicalBond;
import ini.cx3d.physics.PhysicalCylinder;
import ini.cx3d.physics.PhysicalNode;
import ini.cx3d.physics.PhysicalSphere;
import ini.cx3d.utilities.Matrix;


public class LaminationMigrateMod implements LocalBiologyModule {

	private CellElement ce = null;

	private double migrationSpeed;

	private String subName = "GradY";
	private double concThr;

	private double concThrDisp;


	private String thisCellType;

	private Boolean MZStoppingCriterion;

	public void run() {

		PhysicalSphere ps = ce.getPhysical().getAsPhysicalSphere();

		ce.getPhysical().getAsPhysicalSphere().movePointMass(this.migrationSpeed, new double[]{0,1,0});

		if (!MZStoppingCriterion)
		{
			if (MyGradient.getGradient(this.subName).getConcentration(ce.getLocation()) > this.concThr) {

				ps.setInterObjectForceCoefficient(1);
				for (PhysicalNode pnOther: ps.getNeighboringPhysicalNodes()) {
					if (pnOther.isAPhysicalSphere()) {
						PhysicalSphere psOther = pnOther.getAsPhysicalSphere();

						if (Matrix.distance(ps.massLocation, psOther.massLocation) < 20) {
							if (psOther.getSomaElement().getPropertiy("type").equalsIgnoreCase(ce.getPropertiy("type"))) {
								PhysicalBond pb = ps.makePhysicalBondWith(psOther);
								pb.setBreakingPointInPercent(500);
								pb.setDumpingConstant(0);
								pb.setSpringConstant(1);
								ce.setPropertiy("type", "MZ");
								ps.setInterObjectForceCoefficient(1.0);
								break;
							}
						}
					}
				}
				
				ps.setColor(Color.MAGENTA);
				ps.setAdherence(0.00001);
				ps.setMass(0.0000001);
				ps.setDiameter(15);
				ce.addLocalBiologyModule(new LaminationApoptMod());
				ce.removeLocalBiologyModule(this);
			}
		}

		if (MZStoppingCriterion) {
			if (ce.getPropertiy("type").equalsIgnoreCase("layer6")) {
				int MZNeighbors = 0;
				int layer6stopNeighbors = 0;
				for (PhysicalNode pn : ps.getNeighboringPhysicalNodes()) {
					if (pn.isAPhysicalSphere()) {
						PhysicalSphere currPS = pn.getAsPhysicalSphere();
						String nbType = currPS.getSomaElement().getPropertiy("type");
						if (nbType.equalsIgnoreCase("MZ")) {
							MZNeighbors++;
						}
						if (nbType.equalsIgnoreCase("layer6_stopped")) {
							layer6stopNeighbors++;
						}
					}
				}
				if ((MZNeighbors>0)||(layer6stopNeighbors>3)) {
					ce.setPropertiy("type", "layer6_stopped");
					ps.setColor(new Color(0, 64, 255));
					ps.setInterObjectForceCoefficient(1.0);
					ps.setMass(10);
					ps.setDiameter(15);
					ps.setAdherence(0.01);
					ce.addLocalBiologyModule(new LaminationApoptMod());
					ce.removeLocalBiologyModule(this);
				}
			}


			if (ce.getPropertiy("type").equalsIgnoreCase("layer5")) {
				int MZNeighbors = 0;
				int L6Neighbors = 0;
				for (PhysicalNode pn : ps.getNeighboringPhysicalNodes()) {
					if (pn.isAPhysicalSphere()) {
						PhysicalSphere currPS = pn.getAsPhysicalSphere();
						String nbType = currPS.getSomaElement().getPropertiy("type");
						if (nbType.equalsIgnoreCase("MZ")) {
							MZNeighbors++;
						}
						if (nbType.equalsIgnoreCase("layer6_stopped")) {
							L6Neighbors++;
						}
					}
				}
				if ((MZNeighbors>1)&&(L6Neighbors<1)) {
					ce.setPropertiy("type", "layer5");
					ps.setColor(new Color(0, 255, 255));
					ps.setInterObjectForceCoefficient(1);
					ps.setMass(10);
					ps.setDiameter(15);
					ps.setAdherence(1);
					ce.addLocalBiologyModule(new LaminationApoptMod());
					ce.removeLocalBiologyModule(this);
				}
			}

			if (ce.getPropertiy("type").equalsIgnoreCase("layer4")) {
				int layer5Neighbors = 0;
				int MZNeighbors = 0;
				int L6Neighbors = 0;
				for (PhysicalNode pn : ps.getNeighboringPhysicalNodes()) {
					if (pn.isAPhysicalSphere()) {
						PhysicalSphere currPS = pn.getAsPhysicalSphere();
						String nbType = currPS.getSomaElement().getPropertiy("type");
						if (nbType.equalsIgnoreCase("MZ")) {
							MZNeighbors++;
						}
						if (nbType.equalsIgnoreCase("layer5")) {
							layer5Neighbors++;
						}
						if (nbType.equalsIgnoreCase("layer6_stopped")) {
							L6Neighbors++;
						}
					}
				}
				if ((MZNeighbors>1)&&(layer5Neighbors+L6Neighbors<1)) {
					ce.setPropertiy("type", "layer4");
					ps.setColor(new Color(191, 255, 64));
					ps.setInterObjectForceCoefficient(1.0);
					ps.setAdherence(1);
					ps.setMass(10);
					ps.setDiameter(15);
					ce.addLocalBiologyModule(new LaminationApoptMod());
					ce.removeLocalBiologyModule(this);
				}
			}

			if (ce.getPropertiy("type").equalsIgnoreCase("layer3")) {
				int layer4Neighbors = 0;
				int layer5Neighbors = 0; 
				int layer6Neighbors = 0; 
				int MZNeighbors = 0;
				for (PhysicalNode pn : ps.getNeighboringPhysicalNodes()) {
					if (pn.isAPhysicalSphere()) {
						PhysicalSphere currPS = pn.getAsPhysicalSphere();
						String nbType = currPS.getSomaElement().getPropertiy("type");
						if (nbType.equalsIgnoreCase("MZ")) {
							MZNeighbors++;
						}
						if (nbType.equalsIgnoreCase("layer4")) {
							layer4Neighbors++;
						}
						if (nbType.equalsIgnoreCase("layer5")) {
							layer5Neighbors++;
						}
						if (nbType.startsWith("layer6")) {
							layer6Neighbors++;
						}
					}
				}
				if ((MZNeighbors>0)&&((layer4Neighbors+layer5Neighbors+layer6Neighbors)<1)) {
					ce.setPropertiy("type", "layer3");
					ps.setColor(new Color(255,128,0));
					ps.setInterObjectForceCoefficient(1.0);
					ps.setAdherence(2);
					ps.setMass(10);
					ps.setDiameter(15);
					ce.addLocalBiologyModule(new LaminationApoptMod());
					ce.removeLocalBiologyModule(this);
				}
			}

			if (ce.getPropertiy("type").equalsIgnoreCase("layer2")) {
				int layer3Neighbors = 0;
				int layer4Neighbors = 0;
				int layer5Neighbors = 0;
				int layer6Neighbors = 0;
				int MZNeighbors = 0;
				for (PhysicalNode pn : ps.getNeighboringPhysicalNodes()) {
					if (pn.isAPhysicalSphere()) {
						PhysicalSphere currPS = pn.getAsPhysicalSphere();
						String nbType = currPS.getSomaElement().getPropertiy("type");
						if (nbType.equalsIgnoreCase("MZ")) {
							MZNeighbors++;
						}
						if (nbType.equalsIgnoreCase("layer3")) {
							layer3Neighbors++;
						}
						if (nbType.equalsIgnoreCase("layer4")) {
							layer4Neighbors++;
						}
						if (nbType.equalsIgnoreCase("layer5")) {
							layer5Neighbors++;
						}
						if (nbType.startsWith("layer6")) {
							layer6Neighbors++;
						}
					}
				}

				if ((MZNeighbors>0)&&((layer3Neighbors+layer4Neighbors+layer5Neighbors+layer6Neighbors)<1)) {
					ce.setPropertiy("type", "layer2");
					ps.setColor(new Color(191, 0, 0));
					ps.setInterObjectForceCoefficient(1.0);
					ps.setAdherence(2);
					ps.setMass(10);
					ps.setDiameter(15);
					ce.addLocalBiologyModule(new DelayedApoptSubSecretor(10000));
					ce.addLocalBiologyModule(new LaminationApoptMod());
					ce.removeLocalBiologyModule(this);
				}
			}
		}
	}

	public void setMZStoppingCriterion(Boolean stoppingCrit) {
		this.MZStoppingCriterion = stoppingCrit;
	}

	public LaminationMigrateMod(double migrationSpeed, double gradStopThr, double gradStopThrDisp, Boolean MZstopping, String cellType) {
		this.migrationSpeed = migrationSpeed;
		this.concThr = gradStopThr+Math.random()*gradStopThrDisp;
		this.MZStoppingCriterion = MZstopping;
		this.thisCellType = cellType;

	}


	public void setCellElement(CellElement cellElement) {
		this.ce = cellElement;
	}


	public LocalBiologyModule getCopy() {
		return new LaminationMigrateMod(this.migrationSpeed, this.concThr, this.concThrDisp, this.MZStoppingCriterion, this.thisCellType);
	}


	public boolean isCopiedWhenNeuriteBranches() {
		return false;
	}


	public boolean isCopiedWhenSomaDivides() {
		return true;
	}

	public boolean isCopiedWhenNeuriteElongates() {
		return false;
	}

	public boolean isCopiedWhenNeuriteExtendsFromSoma() {
		return false;
	}

	public boolean isDeletedAfterNeuriteHasBifurcated() {
		return false;
	}

	public void setType(String type) {
	}

	public void initGRN() {
	}
}
