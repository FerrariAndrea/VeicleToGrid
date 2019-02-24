package model;


import javafx.scene.paint.Color;

public class NormalParkingSpaceFactory extends ParkingSpaceFactory {

	@Override
	public NormalParkingSpace CreateParkingSpace(int ID) {
		return new NormalParkingSpace(ID);
	}

	private class NormalParkingSpace extends ParkingSpace {

		public NormalParkingSpace(int ID) {
			super(ID);
		}

		@Override
		public Color getColor() {
			return Color.WHITE;
		}
		
		
	}
}
