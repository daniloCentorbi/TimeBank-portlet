package ita.social.portlet;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.StringUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.GenericPortlet;
import javax.portlet.PortletException;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.ProcessAction;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

/**
 * Portlet implementation class NewPortletone
 */
public class NewPortletone extends GenericPortlet {
    protected String viewJSP;
    protected String editJSP;
    private static Log _log = LogFactoryUtil.getLog(NewPortletone.class);
    
	public void init() {
		viewJSP = getInitParameter("view-jsp");
		editJSP = getInitParameter("edit-jsp");
	}

	public void doView(RenderRequest renderRequest,
			RenderResponse renderResponse) throws IOException, PortletException {

		include(viewJSP, renderRequest, renderResponse);
	}

	public void doEdit(RenderRequest renderRequest,
			RenderResponse renderResponse) throws IOException, PortletException {

		include(editJSP, renderRequest, renderResponse);
	}

	protected void include(String path, RenderRequest renderRequest,
			RenderResponse renderResponse) throws IOException, PortletException {

		PortletRequestDispatcher portletRequestDispatcher = getPortletContext()
				.getRequestDispatcher(path);

		if (portletRequestDispatcher == null) {
			_log.error(path + " is not a valid include");
		} else {
			portletRequestDispatcher.include(renderRequest, renderResponse);
		}
	}

	  @ProcessAction(name="saveConf")
	public void saveConf(ActionRequest request, ActionResponse response)
			throws Exception {
		// otteniamo l’oggetto PortletPreferences che "contiene" le preference
		// dell’istanza del nostro portlet
		PortletPreferences portletPreferences = request.getPreferences();
		// Recuperiamo il valore del parametro singolo
		String singolo = ParamUtil.getString(request, "singolo");
		// settiamo il valore del campo "singolo"
		portletPreferences.setValue("singolo", singolo);
		List<String> prefList = new ArrayList<String>();
		// array di indici per le preferences
		int[] prefListIndexes = StringUtil.split(
				ParamUtil.getString(request, "prefListIndexes"), 0);
		for (int i = 0; i < prefListIndexes.length; i++) {
			int prefListIndex = prefListIndexes[i];
			// ciclando sull’array di indici recuperiamo i valori inseriti
			// nelle righe
			String pref = ParamUtil.getString(request, "listaParametri"
					+ prefListIndex);
			prefList.add(pref);
		}
		// creiamo l’array con i valori da salvare nelle preferences..
		String[] prefListArray = new String[prefList.size()];
		for (int i = 0; i < prefListArray.length; i++) {
			prefListArray[i] = prefList.get(i);
		}
		// ...e qui li settiamo
		portletPreferences.setValues("listaParametri", prefListArray);

		// salviamo le preferences sul database
		portletPreferences.store();
		// visualizziamo nuovamente la portlet in edit mode
		response.setRenderParameter("jspPage",
				"/edit.jsp");
	}

}
