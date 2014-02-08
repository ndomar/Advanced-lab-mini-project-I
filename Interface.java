import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

/*
This file contains the code for the GUI and the simple "normal" calculator.
*/

class Interface extends JFrame implements  ActionListener
	 {
	Container c ; 
		JTextField result ;
		JPanel p = new JPanel();
		JButton b[] = new JButton[16];
		
		JLabel variables[] = new JLabel[27];
		
		String s[] = {"0","1","2","3","4","5","6","7","8","9","+","-","/","*","=","C"};
		//----------------------Me------------------------
		String Screen="",monitor1="",monitor2="",OperationOnScreen=""; 
		boolean CommandEmpty=true,switcher=true;
		double R=Integer.MIN_VALUE,L=Integer.MIN_VALUE;
		//------------------------------------------------
		public Interface ()
			{
			super ("Calculator (With speech recognition)");
		
			c=getContentPane();
			result = new JTextField();
			result.setEditable(false);
			result.setBackground(Color.cyan);
			p.setLayout(new GridLayout(7,5));
			for (int i=0;i<16;i++)
			{
				b[i] = new JButton(s[i]);
				b[i].addActionListener(this);
				p.add(b[i]);
			}
			
			for (int i=0; i<27;i++)
			{
				variables[i] = new JLabel("Empty");
				p.add(variables[i]);
			}
			c.add(result,BorderLayout.NORTH);
			c.add(p);
			
			setSize(600,600);
			setVisible(true);
			setResizable(true);
			setLocationRelativeTo(null);
			}//End Constructor
		public static void main (String[] args) 
			{
				new Interface();
	  		}
		// Uncomment this if you want to test the GUI only
	  	//------------------------------------------------				
		public void actionPerformed(ActionEvent event)
			{
				for (int i=0; i<=9; i++)//Numbers
				{
					if(event.getSource()==b[i])
					{					
						Screen+=i;
						result.setText("");
						result.setText(Screen);	
					}
				}
					
				for (int i=10; i<=14; i++)//Commands
				{
					if(event.getSource()==b[i])
					{
						if(result.getText().lastIndexOf(OperationOnScreen)!=-1)//prevent exception
							result.setText(result.getText().substring(0,result.getText().lastIndexOf(OperationOnScreen))+s[i]);
						else
							result.setText(result.getText()+s[i]);
						OperationOnScreen=s[i];	
							
						if(switcher)
						     {monitor1=s[i];switcher=false;}
						else {monitor2=s[i];switcher=true;}
						
						if (monitor1!=monitor2 && monitor2!="")
							{
							if(switcher) //execute older,send sign newer
						   	 	 {Calc(event,monitor1.charAt(0),monitor2); }
							else {Calc(event,monitor2.charAt(0),monitor1); }
						    }
						if(s[i]!="=") //calc returns 0
							Calc(event,s[i].charAt(0),s[i]);
					}					
				}
				
				if(event.getSource()==b[15]) //Clear
				{
					Screen=""; monitor1=""; monitor2=""; 
					switcher=true; CommandEmpty=true;
					result.setText("");
				}
			}//end actionPerformed
			
			public void Calc(ActionEvent event,char OpType,String Operator)
			{		if (Operator=="=")
						Operator="";
						
					if(CommandEmpty && Screen=="")
					{
						return;
					}
						
					else if(CommandEmpty && Screen!="")
					{
						R=Integer.parseInt(Screen);
						result.setText(Screen+Operator);
						Screen="";
						CommandEmpty=false;
					}
					else if(!CommandEmpty && Screen!="")
					{
						L=Integer.parseInt(Screen);
						R=Operations(R,L,OpType);//calculate
						Screen="";
						result.setText("");
						result.setText(R+Operator);
						variables[26].setText("Output = " + R+Operator);
					}	
			}//End Calc				

		public static double  Operations(double R, double L, char op)
		{	
			switch (op)
			{
				case '+':
					return R+L;
				case '-':
					return R-L;
				case '*':
					return R*L;
				case '/':
					return R/L;											
			}
			return 0;
		}
}//end class