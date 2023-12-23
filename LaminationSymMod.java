import ini.cx3d.biology.Cell;
import ini.cx3d.biology.CellElement;
import ini.cx3d.biology.LocalBiologyModule;
import ini.cx3d.physics.PhysicalSphere;


public class LaminationSymMod implements LocalBiologyModule {

	private CellElement ce = null;

	private double maxDiam = 10.00;
	
	private double counterSubQuant;

	@Override
	public void run() {

		counterSubQuant = counterSubQuant*(1-0.01);

		runCellCycleDiffStep();
		
		if (counterSubQuant<65) {
			if (this!=null) {
			ce.addLocalBiologyModule(new LaminationNeuronMod());
			ce.removeLocalBiologyModule(this);
			}
		}
	}


	public void setCellElement(CellElement cellElement) {
		this.ce = cellElement;
	}

	public LocalBiologyModule getCopy() {
		LaminationSymMod siblingMyGRN2 = new LaminationSymMod();
		siblingMyGRN2.setCounterSubQuant(counterSubQuant);
		return siblingMyGRN2;
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

	public void setCounterSubQuant(double quant) {
		this.counterSubQuant = quant;
	}

	private void runCellCycleDiffStep() {

		PhysicalSphere ps = (PhysicalSphere) this.ce.getPhysical();
		double currDiam = ps.getDiameter();
		
		if (currDiam < maxDiam) {
			ps.setDiameter(currDiam+0.2);
		}
		
		else {
			if (counterSubQuant>0.0000001) {
				Cell daughter = ce.getCell().divide(new double[]{0,-1,0});
			}		
		}
	}

}
