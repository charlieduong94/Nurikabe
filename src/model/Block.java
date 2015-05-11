package model;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.MouseListener;

public class Block extends Rectangle{
	boolean blank = true;
	boolean landSource = false;  //if landSource, than it has children in source num > 1
	boolean water = false;
	boolean landStem = false;
	boolean complete = false;
	boolean child = false;
	boolean valid = true;
	Block parent = null;
	int sourceNum = 0;
	MouseListener listener = null;

	Font f = null;
	int fontSize = 1;
	Color color = Color.WHITE;
	public Block(){
		
	}
	
	public Block(int xpos, int ypos, double w, double h){
		x = xpos;
		y = ypos;
		width = (int)w;
		height = (int)h;
	}
	
	
	public void paintComponent(Graphics g){
		//super.paintComponent(g);
		
		//g.drawRect(0, 0, this.getWidth() - 1, this.getHeight() - 1);
		//System.out.println("Hello");
		fontSize = height - height/ 6;
		f = new Font(Font.SANS_SERIF, Font.BOLD, fontSize);
		g.setFont(f);
		if(water){
			
			g.setColor(Color.blue);
			g.fillRect(x, y, width, height);
		}
		if(blank){
			if(!valid){
				g.setColor(Color.red);
			}
			else{
				g.setColor(Color.white);
			}
			
			g.fillRect(x, y, width, height);
		}
		if(landStem){
			if(valid && complete){
				g.setColor(Color.green);
			}
			else if(valid){
				g.setColor(Color.yellow);
			}
			else{
				g.setColor(Color.red);
			}
			g.fillRect(x, y, width, height);
			if(child){
				g.setColor(Color.red);
				g.drawOval(x, y, width, height);
			}
			
			
		}
		if(landSource){
			if(valid && complete){
				g.setColor(Color.green);
			}
			else if(valid && !complete){
				g.setColor(Color.yellow);
			}
			else{
				g.setColor(Color.red);
			}
			g.fillRect(x, y, width, height);
			g.setColor(Color.black);
			if(sourceNum < 10){
				g.drawString(""+ sourceNum, (int) (x + width*0.375), y + height - (height / 6 ));
			}
			else{
				g.drawString(""+ sourceNum, (int) (x + width*0.2), y + height - (height / 6 ));
			}
			if(child){
				g.setColor(Color.red);
				g.drawOval(x, y, width, height);
			}
			
		}
		
	}
	public void setLandSource(int num){
		blank = false;
		water = false;
		landStem = false;
		landSource = true;
		sourceNum = num;
		complete = false;
		child = false;
		parent = this;
	}
	public void setBlank(){
		blank = true;
		water = false;
		landStem = false;
		landSource = false;
		sourceNum = 0;
		complete = false;
		child = false;
		parent = null;
	}
	public void setLandStem(){
		blank = false;
		water = false;
		landSource = false;
		landStem = true;
		sourceNum = 0;
		complete = false;
		child = false;
	}
	public void setWater(){
		blank = false;
		water = true;
		landSource = false;
		landStem = false;
		sourceNum = 0;
		child = false;
		parent = null;
	}
	public void setValidity(boolean b){
		valid = b;
	}
	public void setCompleteness(boolean b){
		complete = b;
	}
	public boolean isLandSource(){
		boolean b = false;
		if(!blank && !water && !landStem && landSource){
			b = true;
		}
		return b;
	}
	public boolean isLandStem(){
		boolean b = false;
		if(!blank && !water && landStem && !landSource){
			b = true;
		}
		return b;
	}
	public boolean isBlank(){
		boolean b = false;
		if(blank && !water && !landStem && !landSource){
			b = true;
		}
		return b;
	}
	public boolean isWater(){
		boolean b = false;
		if(!blank && water && !landStem && !landSource){
			b = true;
		}
		return b;
	}
	public void setChild(boolean b){
		child = b;
	}
	public void setParent(Block b){
		parent = b;
		
	}
	public boolean isChild(){
		return child;
	}
	public boolean isComplete(){
		return complete;
	}
	public boolean isValid(){
		return valid;
	}
	public Block clone(){
		Block b = (Block) super.clone();
		b.blank = this.blank;
		b.landSource = this.landSource;
		b.landStem = this.landStem;
		b.water = this.water;
		b.sourceNum = this.sourceNum;
		b.complete = this.complete;
		b.valid = this.valid;
		return b;
	}
	public void set(Block b){
		this.blank = b.blank;
		this.landSource = b.landSource;
		this.landStem = b.landStem;
		this.water = b.water;
		this.sourceNum = b.sourceNum;
		this.complete = b.complete;
		this.valid = b.valid;
	}
	public boolean equals(Block b){
		boolean equals = true;
		if(this.blank != b.blank){
			equals = false;
		}
		else if(this.landSource != b.landSource){
			equals = false;
		}
		else if(this.landStem != b.landStem){
			equals = false;
		}
		else if(this.water != b.water){
			equals = false;
		}
		else if(this.child != b.child){
			equals = false;
		}
		else if(this.complete != b.complete){
			equals = false;
		}
		else if(this.sourceNum != b.sourceNum){
			equals = false;
		}
		else if(this.valid != b.valid){
			equals = false;
		}
		return equals;
		
	}
}