package team0;

import game.AI;
import game.Board;
import game.Board.ControlKeys;
import game.Shape;
import game.Shape.Tetrominoes;

public class MyAI extends AI {

	final int zero = 0,swap_various = 4 , block_scale = 4,mid_y=1,mid_x=2
			;
	
	//가독성을 위한 정수 0 , 모양 바꿀수 있는 갯수 4 , 블럭의 크기 4 , 블럭의 y축 중심,블럭의 x축 중심
	
    int[][] play_ground , block;
    int last_block_y_point,timer,target_x,target_swap,score_y = -2 , score_x = -1 ,blocking_score = -3, eraseable = 10;
    String mode;
    
    // default constructor must be public
    public MyAI(String[] args) {
        super();
        init(args);
    };
    
    @Override
    protected ControlKeys command() {
        ControlKeys key = ControlKeys.D;
        Shape curPiece = getCurPiece();
        
        timer += 1;//시스템 특성상 블럭생성시 바로 회전이 불가능하다 따라서 모양을 바꿀수 있는 한 줄을 만들어 주고 사용하도록 하자
        if(block_crashed(getCurY()))
        {
        	//System.out.println("crashed");
            scanner();
            calculate(curPiece);
            
            timer = 0;
            set_last_block_y_position(getCurY());
        }
        else
        {
            set_last_block_y_position(getCurY());
            if(timer > 0)
            {
            	if(target_swap > 0)
            	{
            		//System.out.println("swap");
            		key = ControlKeys.DOWN;
            		target_swap -= 1;
            	}
            	else
            	{
            		//System.out.println("move");
            		key = ControlKeys.D;
            		if(mode.equals("s"))
            		{
            			key = ControlKeys.SPACE;
            		}
            		if(target_x > getCurX())
            		{
            			key = ControlKeys.RIGHT;
            		}
            		else if(target_x < getCurX())
            		{
            			key = ControlKeys.LEFT;
            		}
            	}
            }
        }
        
        return key;
    }
    
    public void calculate(Shape target)
    {
    	//System.out.println("now shape : "+get_tetrominoes(target));
    	int i,t,now_shape = get_tetrominoes(target),expect_y , min_y = Board.HEIGHT,max_score = -0x7fffffff,
    			save_score,result_x = 0,result_swap = 0;
    	for(i=zero;i<Board.WIDTH;i+=1)
    	{
    		for(t=0;t<swap_various;t+=1)
    		{
    			//다 떨어졌을때의 y 좌표를 구한다
        		//System.out.println("fall y : "+get_fallen_position_y(now_shape - 1,t,i)+" x :  "+i+" s : "+t);//모양 데이터는 0 부터 시작하므로 -1 , 몇번째 모양 바뀐건지 , 현재 떨어지고 있는 x 좌표
        		expect_y = get_fallen_position_y(now_shape - 1,t,i);
        		if(expect_y < Board.HEIGHT - 1)
        		{
        			min_y = expect_y;
        			save_score = simulate(i,min_y,get_tetrominoes(target)-1, t);
        			if(save_score > max_score)
        			{
        				max_score = save_score;
        				result_x = i ;
        				result_swap = t;
            			System.out.println("max score updated <"+i+","+min_y+"> #"+result_swap+" : "+max_score);
        			}
        		}
    		}
    	}
    	if(now_shape == 3 && (result_swap == 1 || result_swap == 3))
    	{
        	target_x = result_x -1;
    	}/*
    	else if(now_shape == 7 && result_swap == 1)
    	{
        	target_x = result_x + 1;
    	}*/
    	else
    	{
    		target_x = result_x;
    	}
    	target_swap = result_swap;
    }
    
    public int simulate(int x, int y ,int shape, int swap)
    {
    	int[][] virtual_board = new int[Board.HEIGHT][Board.WIDTH];
    	int i=0,t=0,q=0,itq=0,score = 0,return_score = -987654321;
    	for(i=0;i<Board.HEIGHT;i+=1)
    	{
    		for(t=0;t<Board.WIDTH;t+=1)
    		{
    			virtual_board[i][t] = play_ground[i][t] ;
    		}
    	}

    	try
    	{
    		q = swap*block_scale;
    		//System.out.println("draw "+(y-mid_y)+"~"+(y-mid_y+block_scale-1));
        	for(i=y+1;i>=y+1-block_scale-1 && i >= 0;i-=1)
        	{
        		itq = shape*block_scale;
        		for(t=x-mid_x;t<=x-mid_x+block_scale-1 && t<Board.WIDTH;t+=1)
        		{
        			if(block[q][itq] != 0 && i >= 0 && t >= 0)
        			{
        				virtual_board[i][t] = block[q][itq] + 1;
        			}
        			itq+=1;
        		}
        		q+=1;
        	}
        	
        	for(i=y+1;i>=y+1-block_scale-1 && i >= 0;i-=1)
        	{
        		for(t=x-mid_x;t<=x-mid_x+block_scale-1 && t<Board.WIDTH;t+=1)
        		{
        			if(t >= 0)
        			{
            			if(virtual_board[i][t] == 2)
            			{
            				for(q=i-1;q>=0;q-=1)
            				{
            					if(virtual_board[q][t] == 0)
            					{
            						score = score + (i * blocking_score);
            					}
            					else
            					{
            						break;
            					}
            				}
            				virtual_board[i][t] = 1;
            			}
        			}
        		}
        	}
        	//System.out.println("blocking score : "+score);
        	
        	q = swap*block_scale;
    		//System.out.println("draw "+(y-mid_y)+"~"+(y-mid_y+block_scale-1));
        	for(i=y+1;i>=y+1-block_scale-1 && i >= 0;i-=1)
        	{
        		itq = shape*block_scale;
        		for(itq=shape*block_scale;itq<(shape+1)*block_scale;itq+=1)
        		{
        			if(block[q][itq] != 0)
        			{
        				int blocked_num = 0;
    					for(t=0;t<Board.WIDTH;t+=1)
    					{
    						if(virtual_board[i][t] == 0)
    						{
    							blocked_num += 1;
    						}
    					}
    					if(blocked_num > 0)
    					{
        					score = score + (i * score_y ) + blocked_num*score_x;
    					}
    					else
    					{
    						//System.out.println("erase!");
    						score = score + eraseable;
    					}
    					break;
        			}
        		}
        		q+=1;
        	}
        	return_score = score;
    	}
    	catch(Exception e)
    	{
    		System.out.println("Error : "+e.getMessage()+" i : "+i+" t : "+t+" q : "+q+" itq : "+itq);
    	}
    	//System.out.println("s <"+x+","+y+"> sh "+shape+" sw "+swap);
    	show_virtual_board(virtual_board);
    	System.out.println("this test case score : "+score);
    	return return_score;
    }
    
    public void show_virtual_board(int[][] target)
    {
    	int i,t;
    	System.out.println("---virtual");
    	for(i=Board.HEIGHT-1;i>=0;i-=1)
    	{
    		for(t=0;t<Board.WIDTH;t+=1)
    		{
    			if(target[i][t] == 1)
    			{
    				System.out.printf("□");
    			}
    			else
    			{
    				System.out.printf("■");
    			}
    		}
    		System.out.println();
    	}
    }
    
    public int get_fallen_position_y(int shape , int swap , int x)
    {
    	int min_y = Board.HEIGHT - 1,i,t,q;
    	for(i=Board.HEIGHT - 1; i >= 0; i -=1)
    	{
    		if(is_unable(shape, swap, x, i) == false && min_y > i)
    		{
    			min_y = i;
    		}
    		else if(is_unable(shape, swap, x, i) == true && min_y < Board.HEIGHT - 3)
    		{
    			break;
    		}
    	}
    	return min_y;
    }
    
    public boolean is_unable(int shape , int swap , int x , int y)// true : unable , false : able
    {
    	int i,t,check_x,check_y;
    	//System.out.println("shape : "+shape+" swap : "+swap+" x : "+x+" y : "+y);
    	//check_x = x - mid_x;
    	//check_y = y - mid_y;
    	for(i=swap*block_scale;i<(swap+1)*block_scale;i+=1)
    	{
        	//check_x = x - mid_x;
    		for(t=shape*block_scale;t<(shape+1)*block_scale;t+=1)
    		{
    			check_x = (x + (t%block_scale - mid_x));
    			check_y = (y - (i%block_scale - mid_y));
    			//System.out.printf("check <"+x+","+y+"> <"+check_x+","+check_y+"> <"+(t%block_scale - mid_x)+","+(i%block_scale - mid_y)+">");
    			if(block[i][t] != 0)
    			{
    				if(		
    						check_y < 0 ||
    						check_y >= Board.HEIGHT || 
    						check_x < 0 ||
    						check_x >= Board.WIDTH)
    				{
    					//System.out.println(" out");
    					return true;
    				}
    				else if(play_ground[check_y][check_x] == 1)
    				{
    					//System.out.println(" blocked = "+play_ground[check_y][check_x]);
    					return true;
    				}
    			}
    			//check_x += 1;
				//System.out.println();
    		}
    		//check_y+=1;
    	}
    	return false;
    }
    
    public void set_last_block_y_position(int now_position)
    {
    	this.last_block_y_point = now_position;
    }
    
    public boolean block_crashed(int now_position)
    {
    	if(last_block_y_point < now_position)
    	{
    		//System.out.println("l : "+last_block_y_point+" n : "+now_position);
    		return true;
    	}
    	return false;
    }
    
    public void init(String[] args)
    {
    	try
    	{
    		this.score_y = Integer.parseInt(args[0]);
    		this.score_x = Integer.parseInt(args[1]);
    		this.blocking_score = Integer.parseInt(args[2]);
    		this.eraseable = Integer.parseInt(args[3]);
    		this.mode = args[4];
    		System.out.println("y line score : "+score_y+" x line score : "+score_x+" erase score : "+eraseable+" blocking score : "+blocking_score);
    	}
    	catch(Exception e)
    	{
    		System.out.println("Error : "+e.getMessage());
    	}
    	try
    	{
    		int i,t;
    		timer = 0;
    		last_block_y_point = zero;
    		
    		/*
    		 *  1	■ ■ ■ ■
    		 *  0	■ ■ ■ ■
    		 * -1	■ ■ ■ ■
    		 * -2	■ ■ ■ ■
    		 */
    		block = new int[][]  
    		{//		Z			S		I			T		O			L		J
    				 {0,0,0,1 , 0,1,0,0 , 0,0,1,0 , 0,0,0,0 , 0,0,0,0 , 0,1,1,0 , 0,0,1,1},
    				 {0,0,1,1 , 0,1,1,0 , 0,0,1,0 , 0,1,1,1 , 0,0,1,1 , 0,0,1,0 , 0,0,1,0},
    				 {0,0,1,0 , 0,0,1,0 , 0,0,1,0 , 0,0,1,0 , 0,0,1,1 , 0,0,1,0 , 0,0,1,0},
    				 {0,0,0,0 , 0,0,0,0 , 0,0,1,0 , 0,0,0,0 , 0,0,0,0 , 0,0,0,0 , 0,0,0,0},

    				 {0,0,0,0 , 0,0,0,0 , 0,0,0,0 , 0,0,1,0 , 0,0,0,0 , 0,0,0,1 , 0,0,0,0},
    				 {0,1,1,0 , 0,0,1,1 , 1,1,1,1 , 0,1,1,0 , 0,0,1,1 , 0,1,1,1 , 0,1,1,1},
    				 {0,0,1,1 , 0,1,1,0 , 0,0,0,0 , 0,0,1,0 , 0,0,1,1 , 0,0,0,0 , 0,0,0,1},
    				 {0,0,0,0 , 0,0,0,0 , 0,0,0,0 , 0,0,0,0 , 0,0,0,0 , 0,0,0,0 , 0,0,0,0},
    			 
    				 {0,0,0,1 , 0,1,0,0 , 0,0,1,0 , 0,0,1,0 , 0,0,0,0 , 0,0,1,0 , 0,0,1,0},
    				 {0,0,1,1 , 0,1,1,0 , 0,0,1,0 , 0,1,1,1 , 0,0,1,1 , 0,0,1,0 , 0,0,1,0},
    				 {0,0,1,0 , 0,0,1,0 , 0,0,1,0 , 0,0,0,0 , 0,0,1,1 , 0,0,1,1 , 0,1,1,0},
    				 {0,0,0,0 , 0,0,0,0 , 0,0,1,0 , 0,0,0,0 , 0,0,0,0 , 0,0,0,0 , 0,0,0,0},
    			 
    				 {0,0,0,0 , 0,0,0,0 , 0,0,0,0 , 0,0,1,0 , 0,0,0,0 , 0,0,0,0 , 0,1,0,0},
    				 {0,1,1,0 , 0,0,1,1 , 1,1,1,1 , 0,0,1,1 , 0,0,1,1 , 0,1,1,1 , 0,1,1,1},
    				 {0,0,1,1 , 0,1,1,0 , 0,0,0,0 , 0,0,1,0 , 0,0,1,1 , 0,1,0,0 , 0,0,0,0},
    				 {0,0,0,0 , 0,0,0,0 , 0,0,0,0 , 0,0,0,0 , 0,0,0,0 , 0,0,0,0 , 0,0,0,0},
    		};
            play_ground = new int[Board.HEIGHT][Board.WIDTH];
    		for(i=zero;i<Board.HEIGHT;i+=1)
    		{
    			for(t=zero;t<Board.WIDTH;t+=1)
    			{
    				play_ground[i][t] = zero;
    			}
    		}
    	}
    	catch(Exception e)
    	{
    		System.out.println("init Error : "+e.getMessage());
    	}
    }
    
    int get_tetrominoes(Shape shape)
    {
    	if(Tetrominoes.EMPTY == shape.getShape())
    	{
    		return zero;
    	}
    	else if(Tetrominoes.ZSHAPE == shape.getShape())
    	{
    		return 1;
    	}
    	else if(Tetrominoes.SSHAPE == shape.getShape())
    	{
    		return 2;
    	}
    	else if(Tetrominoes.ISHAPE == shape.getShape())
    	{
    		return 3;
    	}
    	else if(Tetrominoes.TSHAPE == shape.getShape())
    	{
    		return 4;
    	}
    	else if(Tetrominoes.SQUARE == shape.getShape())
    	{
    		return 5;
    	}
    	else if(Tetrominoes.LSHAPE == shape.getShape())
    	{
    		return 6;
    	}
    	else if(Tetrominoes.JSHAPE == shape.getShape())
    	{
    		return 7;
    	}
    	return -1;
    }
    
    public void scanner()
    {
    	int i,t;
        System.out.println("----");
        for(i=Board.HEIGHT - 1;i>=zero ;i-=1)
        {
        	for(t=zero;t<Board.WIDTH;t+=1)
        	{
        		try
        		{
        			if(Board.board[i*Board.WIDTH + t] != Tetrominoes.EMPTY)
        			{
        				//System.out.printf("□");
        				play_ground[i][t] = 1;
        			}
        			else
        			{
        				//System.out.printf("■");
        				play_ground[i][t] = zero;
        			}
        			//System.out.println("<"+i+","+t+"> "+Board.board[(i*Board.WIDTH)+t]);
        		}
        		catch(Exception e)
        		{
        			//System.out.println("Error : <"+i+","+t+">");
        		}
        	}
        	//System.out.println("");
        }
    }
}
