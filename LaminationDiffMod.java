
import ini.cx3d.biology.Cell;
import ini.cx3d.biology.CellElement;
import ini.cx3d.biology.LocalBiologyModule;
import ini.cx3d.physics.PhysicalSphere;
import ini.cx3d.simulation.ECM;

public class LaminationDiffMod implements LocalBiologyModule {

	private CellElement ce = null;

	private double maxDiam = 10.00;
	private ParamSet myParams;

	private double diffSubQuant;
	private Boolean canDifferentiate=true;
	private double subQuant = 0.1;

	public static double prolif0commit = 0.7;
	public static double prolif1commit = 0.65;
	public static double prolif2commit = 0.55;
	public static double prolif3commit = 0.5;
	public static double prolif4commit = 0.3;
	
	public static double layer6prog_commit = 0.1;
	public static double layer5prog_commit = 0.2; 
	public static double layer4prog_commit = 0.3; 
	public static double layer3prog_commit = 0.5; 
	public static double layer2prog_commit = 0.7; 
	
	@Override
	public void run() {

		diffSubQuant = diffSubQuant - 0.001;


		PhysicalSphere ps = this.ce.getPhysical().getAsPhysicalSphere();

		double currDiam = ps.getDiameter();

		if (currDiam < this.maxDiam) {
			ce.getPhysical().setDiameter(currDiam+0.6);
		}

		else {

			if (this.myParams.isDividing()) {
				this.myParams.setIsDividing(false);
				Cell daughter = this.ce.getCell().divide(new double[]{0.0,-1.0,0.0});
			}

			else {
				canDifferentiate = true;
				if (diffSubQuant<this.myParams.getDiffSubThr()) {

					double aRandomValue = Math.random();

					double commitmentRatio = this.myParams.getCommitmentRatio();

					if (aRandomValue<commitmentRatio) {					

						if (this.myParams.checkIfDestCellTypeEquals("MZprog")) {	
							ce.setPropertiy("type", "initMZ");
							ps.setInterObjectForceCoefficient(0.0001);
							ps.setMass(1);	
							LaminationMigrateMod migrateMZ= new LaminationMigrateMod(100, 3, 2, false, "initMZ");
							ce.addLocalBiologyModule(migrateMZ);
							this.ce.removeLocalBiologyModule(this);
						}
						
						if (this.myParams.checkIfDestCellTypeEquals("layer6prog")) {	
							ce.setPropertiy("type", "layer6");
							ps.setInterObjectForceCoefficient(0.0001);
							ps.setMass(1);	
							LaminationMigrateMod migrateLayer6= new LaminationMigrateMod(30, 7, 9, true, "layer6");
							ce.addLocalBiologyModule(migrateLayer6);
							this.ce.removeLocalBiologyModule(this);
						}
						
						if (this.myParams.checkIfDestCellTypeEquals("layer5prog")) {

							ce.setPropertiy("type", "layer5");
							ps.setInterObjectForceCoefficient(0.0001);
							ps.setMass(1);
							LaminationMigrateMod migrateLayer5 = new LaminationMigrateMod(30, 16, 6, true, "layer5");
							this.ce.addLocalBiologyModule(migrateLayer5);
							this.ce.removeLocalBiologyModule(this);
						}
						
						if (this.myParams.checkIfDestCellTypeEquals("layer4prog")) {
							ce.setPropertiy("type", "layer4");
							ps.setInterObjectForceCoefficient(0.0001);
							ps.setMass(1);
							LaminationMigrateMod migrateLayer4 = new LaminationMigrateMod(30, 22, 5, true, "layer4");
							ce.addLocalBiologyModule(migrateLayer4);
							ce.removeLocalBiologyModule(this);
						}
						
						if (this.myParams.checkIfDestCellTypeEquals("layer3prog")) {
							ce.setPropertiy("type", "layer3");
							ps.setInterObjectForceCoefficient(0.0001);
							ps.setMass(1);
							LaminationMigrateMod migrateLayer3 = new LaminationMigrateMod(30, 27, 5, true, "layer3");
							ce.addLocalBiologyModule(migrateLayer3);
							ce.removeLocalBiologyModule(this);
						}
						if (this.myParams.checkIfDestCellTypeEquals("layer2prog")) {
							ce.setPropertiy("type", "layer2");
							ps.setInterObjectForceCoefficient(0.0001);
							ps.setMass(1);
							LaminationMigrateMod migrateLayer2 = new LaminationMigrateMod(30, 32, 4, true, "layer2");
							ce.addLocalBiologyModule(migrateLayer2);
							ce.removeLocalBiologyModule(this);
						}
						
						if (this.myParams.getCurrDestinyCellType().startsWith("prolif")) {
							Lamination.apoptosisA1Counter++;
							if (this.myParams.getCurrDestinyCellType().startsWith("prolif0")) {
								Lamination.apoptosisA1L1Counter++;
							}
							if (this.myParams.getCurrDestinyCellType().startsWith("prolif1")) {
								Lamination.apoptosisA1L6Counter++;
							}
							if (this.myParams.getCurrDestinyCellType().startsWith("prolif2")) {
								Lamination.apoptosisA1L5Counter++;
							}
							if (this.myParams.getCurrDestinyCellType().startsWith("prolif3")) {
								Lamination.apoptosisA1L4Counter++;
							}
							if (this.myParams.getCurrDestinyCellType().startsWith("prolif4")) {
							}
							ECM.getInstance().removeFromSimulation(ps.getSomaElement());
							ECM.getInstance().removePhysicalSphere(ps);
							ce.getPhysical().removeLocally();
							ce.removeLocally();	
							ce.cleanAllLocalBiologyModules();
						}

					}

					if (aRandomValue>=commitmentRatio) {
						
						if (this.myParams.checkIfDestCellTypeEquals("MZprog")) {
							this.myParams.setCurrDistinyCellType("prolif0");
							this.myParams.setIsDividing(true);
							this.myParams.setCommitmentRatio(prolif0commit);
							this.myParams.setDiffSubThr(0.1);
							this.setDiffSubQuant(50*subQuant);
							this.canDifferentiate=false;
						}
						
						if ((canDifferentiate)&&(this.myParams.checkIfDestCellTypeEquals("prolif0"))) {
							this.myParams.setCurrDistinyCellType("layer6prog");
							this.myParams.setIsDividing(false);
							this.myParams.setCommitmentRatio(layer6prog_commit);
							this.myParams.setDiffSubThr(0.1);
							this.canDifferentiate = false;
							this.setDiffSubQuant(50*subQuant);
						}
						
						if ((canDifferentiate)&&this.myParams.checkIfDestCellTypeEquals("layer6prog")) {
							this.myParams.setCurrDistinyCellType("prolif1");
							this.myParams.setIsDividing(true);
							this.myParams.setCommitmentRatio(prolif1commit);
							this.myParams.setDiffSubThr(0.1);
							this.setDiffSubQuant(50*subQuant);
							this.canDifferentiate=false;
						}
						
						if ((canDifferentiate)&&(this.myParams.checkIfDestCellTypeEquals("prolif1"))) {
							this.myParams.setCurrDistinyCellType("layer5prog");
							this.myParams.setIsDividing(false);
							this.myParams.setCommitmentRatio(layer5prog_commit);
							this.myParams.setDiffSubThr(0.1);
							this.canDifferentiate = false;
							this.setDiffSubQuant(50*subQuant);
						}
						
						if ((canDifferentiate)&&(this.myParams.checkIfDestCellTypeEquals("layer5prog"))) {	
							this.myParams.setCurrDistinyCellType("prolif2");
							this.myParams.setIsDividing(true);
							this.myParams.setCommitmentRatio(prolif2commit);
							this.myParams.setDiffSubThr(0.1);
							this.setDiffSubQuant(50*subQuant);
							this.canDifferentiate = false;
						}
						
						if ((canDifferentiate)&&(this.myParams.checkIfDestCellTypeEquals("prolif2"))) {
							this.myParams.setCurrDistinyCellType("layer4prog");
							this.myParams.setIsDividing(false);
							this.myParams.setCommitmentRatio(layer4prog_commit);
							this.myParams.setDiffSubThr(0.1);
							this.setDiffSubQuant(50*subQuant);
							this.canDifferentiate = false;

						}
						if ((canDifferentiate)&&(this.myParams.checkIfDestCellTypeEquals("layer4prog"))) {
							this.myParams.setCurrDistinyCellType("prolif3");
							this.myParams.setCommitmentRatio(prolif3commit);
							this.myParams.setIsDividing(true);
							this.myParams.setDiffSubThr(0.1);
							this.setDiffSubQuant(50*subQuant);
							this.canDifferentiate = false;
						}				
						
						if ((canDifferentiate)&&(this.myParams.checkIfDestCellTypeEquals("prolif3"))) {
							this.myParams.setCurrDistinyCellType("layer3prog");
							this.myParams.setIsDividing(false);
							this.myParams.setCommitmentRatio(layer3prog_commit);
							this.myParams.setDiffSubThr(0.1);
							this.setDiffSubQuant(50*subQuant);
							this.canDifferentiate = false;
						}
						
						if ((canDifferentiate)&&(this.myParams.checkIfDestCellTypeEquals("layer3prog"))) {
							this.myParams.setCurrDistinyCellType("prolif4");
							this.myParams.setIsDividing(true);
							this.myParams.setDiffSubThr(0.1);
							this.myParams.setCommitmentRatio(prolif4commit);
							this.setDiffSubQuant(50*subQuant);
							this.canDifferentiate = false;
						}
						
						
						if ((canDifferentiate)&&(this.myParams.checkIfDestCellTypeEquals("prolif4"))) {
							this.myParams.setCurrDistinyCellType("layer2prog");
							this.myParams.setIsDividing(false);
							this.myParams.setDiffSubThr(0.1);
							this.myParams.setCommitmentRatio(layer2prog_commit);
							this.setDiffSubQuant(50*subQuant);
							this.canDifferentiate = false;						
						}
						
						if ((canDifferentiate)&&(this.myParams.checkIfDestCellTypeEquals("layer2prog"))) {
							Lamination.apoptosisA1Counter++;
							Lamination.apoptosisA1L2Counter++;
							ce.removeLocalBiologyModule(this);
						}
					}

				}

			}
		}

	}


	public LaminationDiffMod(ParamSet currParams) {
		this.myParams = currParams;
		this.diffSubQuant = 1;
	}

	public void setCellElement(CellElement cellElement) {
		this.ce = cellElement;
	}

	public void setCanDifferentiate(Boolean canDiff) {
		this.canDifferentiate = canDiff;
	}

	@Override
	public LocalBiologyModule getCopy() {
		LaminationDiffMod copyDiff  = new LaminationDiffMod(this.myParams.getCopy());
		diffSubQuant = diffSubQuant/2;
		copyDiff.setDiffSubQuant(diffSubQuant);
		copyDiff.setCanDifferentiate(this.canDifferentiate);

		return copyDiff;
	}

	@Override
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

	public void setDiffSubQuant(double quant) {
		this.diffSubQuant = quant;
	}

	public double getDiffSubQuant() {
		return this.diffSubQuant;
	}

	private void runCellCycleDiffStep() {

	}
}
