/**
 * @author SUJEN
 */
var Ajax = {
	sendRequest: function(requestType, url, dataParams, async) {
		var dataStr = "";
		jQuery
			.each(dataParams, function(paramName, paramValue) {
				dataStr += paramName + "=" + paramValue + "&";
			});
		jQuery
			.ajax({
				type : requestType,
				url : url,
				data: dataStr,
				async: async,
				success : function(successResult) {
					result = successResult;
				},
				error: function(errorResult) {
					jQuery("#error-message-container").empty().html(errorResult.responseText);
					jQuery("#error-message-dialog").dialog('open');
					result = "Error";
				}
			});
		return result;
	},

	showProcessing : function(msg) {
		jQuery.unblockUI();
		jQuery
				.blockUI({
					message : '<div class="processing"><div class="ajaxIndicator">&nbsp;</div><div>'
							+ msg + '</div></div>',
					overlayCSS : {
						backgroundColor : '#FFF',
						opacity : 0.5
					}
				});
	},

	hideProcessing : function(block_div) {
		jQuery.unblockUI();
	}
}