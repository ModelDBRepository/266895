import java.awt.Color;

import ini.cx3d.biology.CellElement;
import ini.cx3d.biology.LocalBiologyModule;
import ini.cx3d.biology.NeuriteElement;
import ini.cx3d.biology.SomaElement;
import ini.cx3d.physics.IntracellularSubstance;
import ini.cx3d.physics.PhysicalSphere;
import ini.cx3d.simulation.ECM;


public class LaminationNeuronMod implements LocalBiologyModule {

	private CellElement ce = null;
	private double migrationSpeed;
	private double gradStopThr;
	private double gradStopThrDisp;
	
	public static double prolifMZcommit = 0.16;

	public void run() {
		PhysicalSphere ps = ce.getPhysical().getAsPhysicalSphere();
		ps.setColor(Color.GREEN);	
		ps.setMass(1);
		ps.setInterObjectForceCoefficient(0.0001);
		ps.setAdherence(0.0001);
		ParamSet myParams = new ParamSet();
		
		myParams.setCurrDistinyCellType("MZprog");
		myParams.setIsDividing(true);
		myParams.setCommitmentRatio(prolifMZcommit); 
		myParams.setDiffSubThr(0.55);
		LaminationDiffMod diffmod = new LaminationDiffMod(myParams.getCopy());
		diffmod.setDiffSubQuant(4.0);
		ce.addLocalBiologyModule(diffmod);
		ce.removeLocalBiologyModule(this);

	}

	public LaminationNeuronMod() {
	}

	public LaminationNeuronMod(double migrationSpeed, double gradStopThr, double gradStopThrDisp) {
		this.migrationSpeed = migrationSpeed;
		this.gradStopThr = gradStopThr;
		this.gradStopThrDisp = gradStopThrDisp;
	}

	@Override
	public void setCellElement(CellElement cellElement) {
		// TODO Auto-generated method stub
		this.ce = cellElement;
	}

	@Override
	public LocalBiologyModule getCopy() {
		// TODO Auto-generated method stub
		return new LaminationNeuronMod(this.migrationSpeed, this.gradStopThr, this.gradStopThrDisp);
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
		//this.currType = type;
	}


	public void initGRN() {
	}



}
