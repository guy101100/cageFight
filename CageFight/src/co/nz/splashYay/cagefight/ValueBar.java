package co.nz.splashYay.cagefight;

import org.andengine.entity.Entity;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.color.Color;

public class ValueBar extends Entity
{
    private float width, height, progress, padding;
    private Rectangle backgroundRectangle, progressRectangle;
    
    public ValueBar(float xPos, float yPos, float width, float height, VertexBufferObjectManager vbm)
    {
    	super();
    	
    	this.width = width;
    	this.height = height;
    	this.progress = 100;
    	this.padding = 2.0f;
    	
    	backgroundRectangle = new Rectangle(xPos, yPos, width, height, vbm);
    	progressRectangle = new Rectangle(xPos+padding, yPos+padding, width-2*padding, height-2*padding, vbm);
    	
    	backgroundRectangle.setColor(Color.BLACK);
    	progressRectangle.setColor(Color.WHITE);
    	
    	super.attachChild(backgroundRectangle);
    	super.attachChild(progressRectangle);
    }
    
    public void setProgressPercentage(float progress)
    {
    	if(progress > 100)
    		progress = 100;
    	if(progress < 0)
    		progress = 0;
    	
    	float newWidth = (progress / 100) * (width - 2*padding);
    	
    	progressRectangle.setWidth(newWidth);
    }
    
    public void setBackgroundColor(Color color)
    {
    	backgroundRectangle.setColor(color);
    }
    
    public void setProgressColor(Color color)
    {
    	progressRectangle.setColor(color);
    }
    
    @Override
    public void setAlpha(float alpha)
    {
    	backgroundRectangle.setAlpha(alpha);
    	progressRectangle.setAlpha(alpha);
    }
}
