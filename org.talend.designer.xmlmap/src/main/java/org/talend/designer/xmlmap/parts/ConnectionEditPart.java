// ============================================================================
//
// Copyright (C) 2006-2011 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.designer.xmlmap.parts;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.PolygonDecoration;
import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.gef.EditPart;
import org.talend.designer.xmlmap.figures.routers.CurveConnectionRouter;

/**
 * wchen class global comment. Detailled comment
 */
public class ConnectionEditPart extends BaseConnectionEditPart {

    private CurveConnectionRouter curvrRouter;

    @Override
    public EditPart getSource() {
        return super.getSource();
    }

    @Override
    public EditPart getTarget() {
        return super.getTarget();
    }

    @Override
    protected void createEditPolicies() {

    }

    @Override
    protected IFigure createFigure() {
        PolylineConnection connection = new PolylineConnection();
        connection.setTargetDecoration(new PolygonDecoration());
        curvrRouter = new CurveConnectionRouter();
        connection.setForegroundColor(ColorConstants.gray);
        connection.setLineWidth(2);
        connection.setConnectionRouter(curvrRouter);
        return connection;
    }

}
