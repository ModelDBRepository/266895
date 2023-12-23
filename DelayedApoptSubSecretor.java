import ini.cx3d.biology.CellElement;
import ini.cx3d.biology.LocalBiologyModule;
import ini.cx3d.physics.PhysicalSphere;


public class DelayedApoptSubSecretor implements LocalBiologyModule  {

	private CellElement ce = null;
	public int delayCounter = 0;
	public int delayThr = 500;

	public void run() {
		delayCounter++;
		if (delayCounter>delayThr) {
			ce.getPhysical().getAsPhysicalSphere().modifyExtracellularQuantity("apoptosisSub", 9999999);
			ce.removeLocalBiologyModule(this);
		}
	}

	public DelayedApoptSubSecretor(int specDelayThr) {
		this.delayThr = specDelayThr;
	}
	
	@Override
	public void setCellElement(CellElement cellElement) {
		this.ce = cellElement;

	}

	@Override
	public LocalBiologyModule getCopy() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isCopiedWhenNeuriteBranches() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isCopiedWhenSomaDivides() {
		// TODO Auto-generated method stub
		return false;
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
}
