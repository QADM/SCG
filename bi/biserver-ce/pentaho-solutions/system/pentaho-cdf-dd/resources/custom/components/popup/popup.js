


var PopupComponent = BaseComponent.extend({

  ph: undefined,
  arrow: undefined,
  content: undefined,
  cancel: undefined,
  $overlay: undefined,
  
  /* // Default settings
  gravity: undefined,
  draggable: true,
  closeOnClickOutside: false,
  */ 

  update: function(){
    var myself = this;
    this.content = $("#" + this.htmlObject).detach();
    this.ph = this.ph ? this.ph.empty() : $('<div>').appendTo($('body'));
    this.content.appendTo(this.ph);
    this.ph.hide();
    this.ph.addClass('popupComponent');
    this.cancel = $("<a>&nbsp;</a>");
    this.cancel.addClass("close").click(function(){
      myself.hide();
    });
    this.cancel.appendTo(this.ph);
    this.arrow = $("<div class='arrow'>").appendTo(this.ph);
    this.content.removeClass('hidePopup');
  },
  clone: function(params,comps,html) {
    var that = this.base(params,comps,html);
    that.ph = this.ph.clone();
    that.ph.insertAfter(this.ph);
    that.ph.hide();
    that.ph.find("[id]").each(function(i,e){
      $e = $(e);
      var id = $e.attr("id");
      if(id && id in html) {
        $e.attr("id", html[id]);
      } else {
        $e.attr("id",id + '_' + Dashboards.duplicateIndex);
      }
    });
    return that;
  },
  popup: function(target,gravity) {
    var pos = target.offset(),
    css = {
      'top': 'auto',
      'bottom': 'auto',
      'left': 'auto',
      'right': 'auto'
    },
    /* Minimum distance from the edges */
    minimumDistance = 20,
    /* How much clearance we need to display the vertex, 
       * should be (vertex depth - border depth)
       */
    vertexOffset = 18 - 6,
    /* Size of the vertex along the tooltip edge */
    vertexSize = 45, 
    targetOffset,
    phHeight = this.ph.outerHeight(),
    phWidth = this.ph.outerWidth();
 
    /* Allow overriding this.gravity */
    gravity = gravity || this.gravity;
    
    /* Draggable/ */
    var draggable = typeof this.draggable === "undefined"?true:this.draggable;

    /* Horizontal Scrollbar */
    if(this.horizontalScroll){
      $("#"+this.htmlObject).css("overflow-x","scroll");
    }

    /* Vertical Scrollbar */
    if(this.verticalScroll){
      $("#"+this.htmlObject).css("overflow-y","scroll");  
    }
    
    /* Prevent scrollbars from dragging popup */
    $("#"+this.htmlObject).parent().draggable({cancel:"#"+this.htmlObject});
    
    /* Close on click outside */
    var closeOnClickOutside = typeof this.closeOnClickOutside === "undefined"?false:this.closeOnClickOutside;
    
    
    /* Clear positioning for the arrow */
    this.arrow.css({
      top: "", 
      left: "", 
      bottom: "", 
      right: ""
    });
    this.arrow.show();
    this.ph.removeClass('north south east west');
    /* The gravity parameter is what decides where the tooltip
     * attaches to the target element. The tooltip is positioned
     * by setting the tooltip's top and left properties.
     * 
     * For horizontal (E/W) attachment we calculate top so that the
     * tooltip lies centered with the target, and calculate left
     * such that the tooltip will be adjacent to the target, on the
     * E/W side as appropriate. If top is such that the tooltip would
     * lie outside the viewport, we correct it so that it lies at least
     * at minimumDistance pixels from the edges. If left would imply
     * that the tooltip would be outside the viewport, we reverse the
     * gravity.
     *
     * Then we do vertical positioning for the arrow pointing towards
     * the element. Here we'll just center it vertically with the target.
     *
     * For vertical (N/S) attachment, reverse the vertical and horizontal
     * axes.
     */
    var minWidth = minimumDistance,
    maxWidth = $(document).width() - minimumDistance,
    minHeight = minimumDistance,
    maxHeight = $(document).height() - minimumDistance,
    targetWidth, targetHeight,
    paddingNear, paddingFar;

    switch(gravity) {
      /*************** NORTH ***************/
      case 'N':
        paddingNear = parseInt(target.css('padding-top').replace(/(.*)px/,"$1"),10);
        css.left = this.center(target.outerWidth(),phWidth,pos.left,minWidth,maxWidth);
        targetHeight = "ownerSVGElement" in target[0] ?
        (target.attr("height") ? target.attr("height") - 0 : 0):
        target.height();
        targetOffset = pos.left - css.left - this.ph.css('border-top-width').replace(/(.*)px/,"$1"); 
        css.top = this.offset(targetHeight,phHeight,pos.top+paddingNear,vertexOffset,minHeight,maxHeight,'near');
        this.arrow.css('left',this.center(target.outerWidth(),vertexSize,targetOffset,0,phWidth));
        this.ph.addClass(css.top < pos.top ? 'north':'south');
        break;

      /*************** SOUTH ***************/
      case 'S':
        paddingNear = parseInt(target.css('padding-top').replace(/(.*)px/,"$1"),10);
        targetHeight = "ownerSVGElement" in target[0] ?
        (target.attr("height") ? target.attr("height") - 0 : 0):
        target.height();
        css.left = this.center(target.outerWidth(),phWidth,pos.left,minWidth,maxWidth);
        css.top = this.offset(targetHeight,phHeight,pos.top+paddingNear,vertexOffset,minHeight,maxHeight,'far');
        targetOffset = pos.left - css.left - this.ph.css('border-top-width').replace(/(.*)px/,"$1"); 
        this.arrow.css('left',this.center(target.outerWidth(),vertexSize,targetOffset,0,phWidth));
        this.ph.addClass(css.top < pos.top ? 'north':'south');
        break;

      /*************** WEST ***************/
      case 'W':
        paddingNear = parseInt(target.css('padding-left').replace(/(.*)px/,"$1"),10);

        css.top = this.center(target.outerHeight(),phHeight,pos.top,minHeight,maxHeight);
        targetWidth = "ownerSVGElement" in target[0] ?
        (target.attr("width") ? target.attr("width") - 0 : 0):
        target.width();
        css.left = this.offset(target.width(),phWidth,pos.left+paddingNear,vertexOffset,minWidth,maxWidth,'near');
        targetOffset = pos.top - css.top - this.ph.css('border-left-width').replace(/(.*)px/,"$1");  
        this.arrow.css('top',this.center(target.outerHeight(),vertexSize,targetOffset,0,phHeight));
        this.ph.addClass(css.left < pos.left ? 'west':'east');
        break;

      /*************** EAST ***************/
      case 'E':
        paddingNear = parseInt(target.css('padding-left').replace(/(.*)px/,"$1"),10);
        css.top = this.center(target.outerHeight(),phHeight,pos.top,minHeight,maxHeight);
        targetWidth = "ownerSVGElement" in target[0] ?
        (target.attr("width") ? target.attr("width") - 0 : 0):
        target.width();
        css.left = this.offset(targetWidth,phWidth,pos.left+paddingNear,vertexOffset,minWidth,maxWidth,'far');
        targetOffset = pos.top - css.top - this.ph.css('border-left-width').replace(/(.*)px/,"$1");  
        this.arrow.css('top',this.center(target.outerHeight(),vertexSize,targetOffset,0,phHeight));
        this.ph.addClass(css.left < pos.left ? 'west':'east');
        break;
    }
    this.ph.css(css);
    this.ph.show();

    var escHandler,
    myself = this;
    escHandler = function(e) {
      if (e.which == 27) {
        myself.ph.hide();
        $(document).unbind('keydown',escHandler);
      }
    };
    $(document).keydown(escHandler);
    var dragHandler;
    dragHandler = function() {
      myself.arrow.hide();
    }
    this.ph.bind('drag',dragHandler);
    
    if(draggable){
      this.ph.draggable();    
    }
    var basePos,dragPos;
    this.ph.bind('touchstart',function(e){
      basePos = myself.ph.offset();
      dragPos = {
        left: e.originalEvent.touches[0].pageX, 
        top: e.originalEvent.touches[0].pageY
        };
    });
    this.ph.bind('touchmove',function(e){
      var finalPos = {
        top: basePos.top + e.originalEvent.touches[0].pageY - dragPos.top,
        left: basePos.left + e.originalEvent.touches[0].pageX - dragPos.left
      };
      myself.ph.offset(finalPos);
      myself.arrow.hide();
      e.preventDefault();
    });
    

    if(closeOnClickOutside){
        
      // Define an overlay so that we can click
      if(!this.$overlay){
        this.$overlay = $('<div id="popupComponentOverlay"></div>');
      }
      this.$overlay.appendTo("body").click(function(event){
        event.stopPropagation();
        myself.hide();
      })
    }
    
  },

  hide: function() {
    
    this.ph.hide();
    if(this.$overlay){
      this.$overlay.unbind('click');
      this.$overlay.detach();
      
    }
  },

  /* Given the size (width/height) for a target and a placeholder element,
   * the target's offset (left/top) and minimum/maximum values for the
   * available size, calculates the offset for the placeholder such that
   * the placeholder will be as close to centered relative to the target
   * as the available space allows.
   */
  center: function(targetSize, phSize, offset,min,max) {
    var candidate = offset + targetSize / 2 - phSize/2;
    return candidate + phSize > max ? max - phSize : candidate < min ? min : candidate;
  },

  offset: function(targetSize,phSize,offset,gap,min,max,range) {
    var near = offset - phSize - gap,
    far = offset + targetSize + gap,
    nearAdmissible = near > min,
    farAdmissible = far + phSize < max;

    return range == 'near' ? (nearAdmissible || !farAdmissible ? near : far) :
    range == 'far' ? (farAdmissible || !nearAdmissible ? far : near) :
    near;
  }
});


var ExportPopupComponent = PopupComponent.extend({

  ph: undefined,
  arrow: undefined,
  content: undefined,
  cancel: undefined,
  dataComponent: undefined,
  chartComponent: undefined,
  baseSize: 200,
  scalingFactor: 1.5,

  clone: function(parameterRemap,componentRemap,htmlRemap) {
    var dataComponent = this.dataComponent,
        chartComponent = this.chartComponent;
    delete this.dataComponent;
    delete this.chartComponent;
    var that = this.base(parameterRemap,componentRemap,htmlRemap);
    this.dataComponent = dataComponent;
    that.dataComponent = componentRemap[dataComponent.name] || dataComponent;
    this.chartComponent = chartComponent;
	var truncated = /render_(.*)/.test(chartComponent.name) ?
		chartComponent.name.match(/render_(.*)/)[1]:
		null;
	if(componentRemap[chartComponent.name]) {
	  that.chartComponent = Dashboards.getComponentByName(componentRemap[chartComponent.name]);
	  that.chartExportComponent = componentRemap[chartComponent.name];
	} else if(truncated && componentRemap[truncated]) {
	  that.chartComponent = Dashboards.getComponentByName("render_" + componentRemap[truncated]);
	  that.chartExportComponent = componentRemap[truncated];
	} else {
	  that.chartComponent = chartComponent;
	}
    that.chartComponent = componentRemap[chartComponent.name] || chartComponent;
    return that;
  },

  update: function(){
    var myself = this;
    if (this.ph) {
      this.ph.remove();
    }
    
    this.chartComponent = window["render_"+this.chartExportComponent];
    this.dataComponent = window["render_"+this.dataExportComponent];
        
    this.ph = $('<div>');
    $("#" + this.htmlObject).empty();
    var link = $('<div class="popupTitle">');
    link.text(this.title || 'Export');
    link.click(function(e) {
      myself.popup(link);
      e.stopPropagation();
    })
    $("#" + this.htmlObject).append(link);
    
    
    if (this.chartComponent) {
      var realChartExportLabel = "Export Chart";
      if (this.chartExportLabel && this.chartExportLabel.length > 0)
        realChartExportLabel = this.chartExportLabel;
      var chartExportElt = $('<div class="exportElement">');
      chartExportElt.text(realChartExportLabel);
      chartExportElt.click(function() {
        myself.exportChart();
      });
      chartExportElt.appendTo(myself.ph);
    }
    
    if (this.dataComponent) {
      var realTableExportLabel = "Export Data";
      if (this.dataExportLabel && this.dataExportLabel.length > 0)
        realTableExportLabel = this.dataExportLabel;
      var dataExportElt = $('<div class="exportElement">');
      dataExportElt.text(realTableExportLabel);
      dataExportElt.click(function() {
        myself.exportData();
      });
      dataExportElt.appendTo(myself.ph);
    }
    
    
    
    $(this.contentLinks).each(function (i, elt) {
      var popupElt = $('<div class="exportElement">');
      popupElt.text(elt[0]);
      popupElt.click(elt[1]);
      popupElt.appendTo(myself.ph);
    });
    
    //    this.content = .appendTo(this.ph);
    this.ph.hide().appendTo($('body'));
    this.ph.addClass('popupComponent');
    this.ph.addClass('exportOptions');
    this.cancel = $("<a>&nbsp;</a>");
    this.cancel.addClass("close").click(function(){
      myself.hide();
    });
    
    
    this.cancel.appendTo(this.ph);
    this.arrow = $("<div class='arrow'>").appendTo(this.ph);
  //    this.content.removeClass('hidePopup');
    
  },
  
  popup: function(target,gravity) {
    this.base(target, gravity);
   	
    var myself = this;
      
    var docClick = function (e) {
      var x = e.pageX;
      var y = e.pageY;
      var linkPos = $("#" + myself.htmlObject).position();

      if ((x < linkPos.left || x > linkPos.left + $("#" + myself.htmlObject).width()) ||
        (y < linkPos.top || y > linkPos.top + $("#" + myself.htmlObject).height())) {
        myself.hide();            
        $(document).unbind('click', docClick);
      }
    };            
    $(document).click(docClick);
  
  },
  
  
  exportData: function(det){

    var effectiveExportType = det == undefined ? this.dataExportType : det ;   

    // Get query
    Dashboards.log("Exporting to " + effectiveExportType);

    var parameters = this.dataComponent.parameters;
    var cd = ( this.dataComponent.chartDefinition ) ? this.dataComponent.chartDefinition : this.dataComponent.queryDefinition;
    var dataAccess = cd.dataAccessId;
    var path = cd.path;

    var url = "../cda/doQuery?path="+path+"&dataAccessId="+dataAccess+"&outputType=" + effectiveExportType + "&settingattachmentName="+this.dataExportAttachmentName+"." + effectiveExportType;
    // Get parameter values; metadata is a special parameter, carries important
    // info for dashboard operation but has no data so isn't exported
    
    var doQueryParameters = {};
    for(var i=0; i<parameters.length; i++){
        var paramName = 'param' + parameters[i][0];
        doQueryParameters[paramName] = parameters[i][0] != 'metadata' ?
          Dashboards.ev(Dashboards.getParameterValue(parameters[i][1])) :
          'false';
    }

    var theDoQuery = url + '&wrapItUp=wrapit';
    $.post(theDoQuery, doQueryParameters, function(uuid) {
        var _exportIframe = _exportIframe || $('<iframe style="display:none">');
        _exportIframe.detach();
        _exportIframe[0].src = webAppPath + '/content/cda/unwrapQuery?' + $.param( {"path": cd.path, "uuid": uuid});
        _exportIframe.appendTo($('body'));
    });


  },  
  
  
  
  exportChart: function(cet){

    var effectiveExportType = cet == undefined ? this.chartExportType : cet ;   

    // Get query
    Dashboards.log("Exporting to " + effectiveExportType);

    var parameters = this.chartComponent.parameters;
    var dataAccess = this.chartComponent.chartDefinition.dataAccessId;
    var path = this.chartComponent.chartDefinition.path;

    var loc = (Dashboards.getQueryParameter("solution") + "/" + Dashboards.getQueryParameter("path") + "/").replace(/\/\//g,"/");

    var url = "../cgg/draw?script="+ loc +  this.chartExportComponent + ".js&outputType=" + effectiveExportType;
    var param;
    // Get parameter values; metadata is a special parameter, carries important
    // info for dashboard operation but has no data so isn't exported
    for(var i=0; i<parameters.length; i++){
      param = Dashboards.ev(Dashboards.getParameterValue(parameters[i][1]));
      if( param !== undefined ){
        url += "&param" + parameters[i][0] + "=" + (parameters[i][0] != 'metadata' ? encodeURIComponent( param ) : 'false');
      }
    }

    var myself = this;
    var masterDiv = $('<div class="exportChartMasterDiv">');
    //Style later
    var totalWidth = Math.max(700, this.chartComponent.chartDefinition.width);
    var popupButtonsDiv = $("<div class='exportChartPopupButtons' style='width:" +totalWidth + "px'>");
    masterDiv.append(popupButtonsDiv);
   
    var titleDiv = $("<div class='exportChartTitle'>Export Options</div>");
    popupButtonsDiv.append(titleDiv);
   
    var smallButton = $("<div class='exportChartPopupButton exportChartButtonNotLast'>Small</div>");
    smallButton.click(function () {
      $('.exportChartPopupButtonClicked').each(function (i, elt) {
        $(elt).removeClass('exportChartPopupButtonClicked')
      })
      $(this).addClass('exportChartPopupButtonClicked');      
      $('#width').attr('disabled', true); 
      $('#height').attr('disabled', true); 
      
      $('#width').val(myself.baseSize);
      $('#height').val(myself.baseSize*(myself.chartComponent.chartDefinition.height/myself.chartComponent.chartDefinition.width));      
      
      
    //             $('.exportChartOkButton').addClass('exportChartOkButtonDisabled');

   
    });
    popupButtonsDiv.append(smallButton);

    var mediumButton = $("<div class='exportChartPopupButton exportChartButtonNotLast exportChartButtonMiddle'>Medium</div>");
    mediumButton.click(function () {
     
      $('.exportChartPopupButtonClicked').each(function (i, elt) {
        $(elt).removeClass('exportChartPopupButtonClicked')
      })
      $(this).addClass('exportChartPopupButtonClicked'); 
    
      $('#width').attr('disabled', true); 
      $('#height').attr('disabled', true); 
      var size = myself.baseSize * myself.scalingFactor;
      $('#width').val(size);
      $('#height').val(size*(myself.chartComponent.chartDefinition.height/myself.chartComponent.chartDefinition.width));      
    
    
    });
   
    mediumButton.getComponentData = function () {
      return [(myself.chartComponent.chartDefinition.width), (myself.chartComponent.chartDefinition.height)];
    }
   
   
    popupButtonsDiv.append(mediumButton);

   
    var largeButton = $("<div class='exportChartPopupButton exportChartButtonNotLast exportChartButtonMiddle'>Large</div>");
    largeButton.click(function () {
      $('.exportChartPopupButtonClicked').each(function (i, elt) {
        $(elt).removeClass('exportChartPopupButtonClicked')
      })
      $(this).addClass('exportChartPopupButtonClicked');      
    
      $('#width').attr('disabled', true); 
      $('#height').attr('disabled', true);
      
      var size = myself.baseSize * myself.scalingFactor * myself.scalingFactor;
      $('#width').val(size);
      $('#height').val(size*(myself.chartComponent.chartDefinition.height/myself.chartComponent.chartDefinition.width));      
      
    //       $('.exportChartOkButton').addClass('exportChartOkButtonDisabled');    

    });

    popupButtonsDiv.append(largeButton);
   
    var customButton = $("<div class='exportChartPopupButton exportChartButtonMiddle'>Custom</div>");
    customButton.click(function () {
      $('.exportChartPopupButtonClicked').each(function (i, elt) {
        $(elt).removeClass('exportChartPopupButtonClicked')
      })
      $(this).addClass('exportChartPopupButtonClicked'); 
      $('#width').removeAttr('disabled'); 
      $('#height').removeAttr('disabled'); 
    
      $('#width').val(myself.chartComponent.chartDefinition.width);
      $('#height').val(myself.chartComponent.chartDefinition.height);      
    
    
    //    $('.exportChartOkButton').removeClass('exportChartOkButtonDisabled');
          
    });
   
    popupButtonsDiv.append(customButton);

    var inputsWidthDiv = $("<div class='exportChartInput'>&nbsp;&nbsp;&gt;&nbsp;&nbsp;&nbsp;Width:&nbsp;<input id='width'  disabled='true' style='width:50px' value='" + this.chartComponent.chartDefinition.width + "' onChange='javascript:$(\"#height\").val($(\"#width\").val() * " + (myself.chartComponent.chartDefinition.height/myself.chartComponent.chartDefinition.width) + ");' type='text'></div>");
    popupButtonsDiv.append(inputsWidthDiv);   
    var inputsHeightDiv = $("<div class='exportChartInput'>Height:&nbsp;</span><input id='height' disabled='true' style='width:50px' value='" + this.chartComponent.chartDefinition.height + "' type='text'></div>");
    popupButtonsDiv.append(inputsHeightDiv);   
    var okButton = $("<div class='exportChartPopupButton exportChartOkButton'>Export</div>");
    okButton.click(function() {    
      var dimensions, size;
      
      switch ($('.exportChartPopupButtonClicked').text()) {
        case "Small":
          dimensions = [myself.baseSize, myself.BaseSize*(myself.chartComponent.chartDefinition.height/myself.chartComponent.chartDefinition.width)];            
          break;
        case "Medium":
          size = myself.baseSize * myself.scalingFactor;
          dimensions = [size, size*(myself.chartComponent.chartDefinition.height/myself.chartComponent.chartDefinition.width)];            
          break;
        case "Large":
          size = myself.baseSize * myself.scalingFactor * myself.scalingFactor;
          dimensions = [size, size*(myself.chartComponent.chartDefinition.height/myself.chartComponent.chartDefinition.width)];            
          break;        
        case "Custom":
        default:
          dimensions = [$('#width').val(), $('#height').val()];
          break;
      }
      
    
      var _exportIframe =  $('<iframe style="display:none">');
      _exportIframe.detach();
      _exportIframe[0].src = url + "&attachmentName=" +myself.dataExportAttachmentName + "." + effectiveExportType + "&paramwidth=" + dimensions[0] + '&paramheight=' + dimensions[1];
      _exportIframe.appendTo($('body'));     
    
    
    });
    popupButtonsDiv.append(okButton);   
    
   

    var img = $(
      "<img src='" + url +
      "&paramwidth="+ this.chartComponent.chartDefinition.width +"&paramheight="+ this.chartComponent.chartDefinition.height +
      "'/>");
   
    var imgDiv = $("<div class='exportChartImageDiv'>");
    imgDiv.append(img);
    imgDiv.append("&nbsp;");
    masterDiv.append(imgDiv);
    
    $.fancybox({
      type:"html",
      content: masterDiv,
      width:totalWidth ,
      height: this.chartComponent.chartDefinition.height  + 60
    });

  }
  
});
