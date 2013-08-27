
// Init namespaces
wd.marketplace = wd.marketplace || {};
wd.marketplace.panels = wd.marketplace.panels || {};
wd.marketplace.components = wd.marketplace.components || {};
wd.marketplace.actions = wd.marketplace.actions || {};
wd.marketplace.popups = wd.marketplace.popups || {};

            
            
/*
 *
 *  Main application and engine
 *
 */ 
            
var marketplace = wd.caf.application({
    name: 'Marketplace', 
    container:"#marketplace", 
    template:'marketplaceTemplate', 
    transition: "basic"
});


wd.marketplace.engine = function(myself,spec){
    
    /** @private*/
    var impl = myself.engine = {};

    var installedPanel,allPanel;
    
    // start
    
    
    
    impl.init = function(){
        
        $("html").addClass(active_theme);
        // Add pentaho style
        $("body").addClass("pentaho-page-background");
        
        wd.debug("intializing marketplace panel");
        
        installedPanel = myself.panelEngine.getPanel("installedPluginsPanel");
        allPanel = myself.panelEngine.getPanel("allPluginsPanel");
        
        
        //myself.notificationEngine.getNotification().debug("Starting engine");
        
        // Call refresh
        impl.refresh();

        $(".cafPanel.cafLinks:first").addClass('active');
        
        
    }
    
    
    impl.refresh = function(){
       
        wd.info("Refreshing");
       
        // 1. Notify panels to show connecting info
        installedPanel.showConnectingComponent();
        allPanel.showConnectingComponent();
        
        // 2. getPluginList, passing callback
        impl.getPluginList();
    
    }
    
    impl.getAllPanel = function(){
        return allPanel;
    }
    
    impl.getPluginList = function(){
        
        
        $.ajax({
            url: "../getpluginsjson",
            dataType: 'json',
            data: [],
            success: impl.processPluginListResponse,
            error: impl.errorUpdating
        });
        
        
    }
    
    
    impl.processPluginListResponse = function(json){
        
        wd.log("Response: " + json);
        
        // 1. Clean plugins
        installedPanel.cleanPlugins();
        allPanel.cleanPlugins();

        // 2. Add plugins to panel

        json.map(function(plugin){
           
            allPanel.addPlugin(plugin);
            if(plugin.installed){
                installedPanel.addPlugin(plugin);
            }
            
        });

    }
    
    
    impl.installPlugin = function(pluginId, branchId, callbackSuccess, callbackError){

        wd.info("Marketplace engine: installing plugin: " + pluginId + " (" + branchId + ")");
        
        $.ajax({
            url: "../installpluginjson",
            dataType: 'json',
            data: {
                pluginId: pluginId, 
                versionId: branchId
            },
            success: function(jqXHR, textStatus){
                if ( jqXHR.code !== 'PLUGIN_INSTALLED'){
                    callbackError(jqXHR, textStatus);
                } else {
                    callbackSuccess(jqXHR, textStatus);
                }
            },
            error: callbackError
        });
        
    }
    

    impl.uninstallPlugin = function(pluginId, callbackSuccess, callbackError){
        
        wd.info("Marketplace engine: uninstalling plugin: " + pluginId);
        
        $.ajax({
            url: "../uninstallpluginjson",
            dataType: 'json',
            data: {
                pluginId: pluginId
            },
            success: function(jqXHR, textStatus){
                if ( jqXHR.code !== 'PLUGIN_UNINSTALLED'){
                    callbackError(jqXHR, textStatus);
                } else {
                    callbackSuccess(jqXHR, textStatus);
                }
            },
            error: callbackError
        });
        

    }
    
    impl.errorUpdating = function(jqXHR, textStatus, errorThrown){
        if(jqXHR.responseText.search('Login Error')){
            myself.notificationEngine.getNotification().error("Session timeout. Please login again.");
        } else {
            myself.notificationEngine.getNotification().error("Error upgrading - try again later: " + errorThrown);    
        } 
    }
    
}

// Apply mixin
wd.marketplace.engine(marketplace);



/*
 *
 *  Template
 *
 */ 

wd.marketplace.template = function(spec){
    
    /**
     * Specific specs
     */
    
    var _spec = {
        name:"marketplaceTemplate",
        cssFile: undefined
    }

    spec = $.extend({},_spec,spec);
    var myself = wd.caf.template(spec);


    // myself.addActions = function(){}; // nothing

    /**
     * Creates the main structure of the template
     * @name createMainSections
     * @memberof wd.caf.template
     * 
     */
    myself.createMainSections = spec.createMainSections || function(){
        
        
        var wrapper = $('<div class="templateWrapper"></div>');
        var header  = $('<div class="templateHead"></div>').appendTo(wrapper);

        myself.$logo = $('<div class="templateLogo"><span>Marketplace</span></div>').appendTo(header);
        myself.$actions = $('<div class="templateActions"></div>').appendTo(header);
        myself.$panels = $('<div class="templatePanels"></div>').appendTo(header);
        myself.$title = $('<div class="templateTitle contrast-color"></div>').appendTo(header);
  
        header.append($('<div class="templateHShadow"></div>'));
        
        myself.$toggleActionContainer = $('<div class="templateToggleActionContainer"></div>')
        .appendTo(wrapper);
        
        myself.$panelsContainer = $('<div class="templatePanelsContainer"></div>')
        .appendTo(wrapper);
        
        
        return wrapper;
        
    }
    


    myself.drawPanelOnContainer = spec.drawPanelOnContainer || function(panel){
    
        var container = $('<div class="panelContainer"></div>').appendTo(myself.$panelsContainer);
        myself.$panelsContainer.append(panel.draw(container));
        panel.setPlaceholder(container);


    }
    // TODO: put some fade effects?

    return myself;
};

marketplace.getRegistry().registerTemplate(wd.marketplace.template());


/*
 *
 *  Actions
 *
 */ 

marketplace.getRegistry().registerAction( wd.caf.action({
    name: "refresh",
    description: "<div class='actionRefresh' title='Refresh'><img class='image'/></div>",
    order: 10,
    executeAction: function(){
        this.caf.engine.refresh();
    }
}) );








wd.marketplace.actions.toggleAction = function(spec){
    
    /**
     * Specific specs
     */
    
    var _spec = {
        name: "about",
        description: "About action",
        toggleText: "Information",
        order: 110,
        container: undefined 
    };

      
    spec = $.extend({},_spec,spec);    
    var myself = wd.caf.action(spec);
   
    var $actionPh;
   
    
    myself.init = function(caf){

        myself.log("Generic entity init","debug");
        myself.caf = caf;
        
        if (!$actionPh){
            myself.setupAction();
        }   
    }
    
    /**
     * File operations execute action
     * @name executeAction
     * @memberof wd.caf.impl.actions.alertAction
     * 
     */

    myself.executeAction = function(){

        myself.toggle();
        
    } 
    
    
    myself.hide = function (){   
        if(Modernizr.csstransitions){          
            $actionPh.addClass("marketplaceTransparent marketplaceMoveOut");
        }
        else{
            $actionPh.hide();
        }    
    }
    
    myself.show = function (){  
        if(Modernizr.csstransitions){          
            $actionPh.removeClass("marketplaceTransparent marketplaceMoveOut");
        }
        else{
            $actionPh.show();
        } 
    }
    
    myself.toggle = function (){  
        if(Modernizr.csstransitions){          
            $actionPh.toggleClass("marketplaceTransparent marketplaceMoveOut");
        }
        else{
            $actionPh.toggle();
        }  
    }
     
    myself.setupAction = function(){
        
        
        // TODO: Change the way the placeholder is passed? 
        //       This way the action is not dependent on the template.
        var container = spec.container || myself.caf.templateEngine.getTemplate().$toggleActionContainer;
        
        $actionPh = $('<div/>').addClass(Modernizr.csstransitions? 'toggleActionWrapper marketplaceTransparent marketplaceMoveOut': 'toggleActionWrapper marketplaceTransparent')
        .append( $('<div/>').addClass('toggleActionDesc')
            .text( spec.toggleText ) )
        .append( $('<div/>').addClass('toggleActionDevBy') );

        if(!Modernizr.csstransitions){
            $actionPh.css('display','none'); 
        }
       
        container.append($actionPh);
    }
        
    return myself;
        
};

marketplace.getRegistry().registerAction( wd.marketplace.actions.toggleAction({
    name: "about",
    description: "<div class='actionAbout' title='Information'><img class='image'/></div>",
    toggleText: 'Pentaho Marketplace allows you to browse through available plugins and customize your Pentaho installation. Enjoy!',
    order: 20,
    container: marketplace.getRegistry()
    .getTemplate( marketplace.options.template ).$toggleActionContainer
}) );




/*
 *
 *  Components
 *
 */ 

wd.marketplace.components.infoDiv = function(spec) {


    /**
     * Specific specs
     */
    
    var _spec = {
        name: "override",
        type: "component",
        description: "override description",
        pulsatePeriod: undefined
    };
    
    spec = $.extend({},_spec,spec);
    var myself = wd.caf.component(spec);
    
    var isDrawn;
   
    myself.draw = function ($ph) {
        if (!isDrawn){
            var $actionPh = $('<div/>').addClass('infoDivWrapper').text( spec.description );
            $ph.append($actionPh);
            
            isDrawn = true;
            
            if (spec.pulsatePeriod){
                myself.pulsate($actionPh);
            }
        }   
    }
    
    myself.pulsate = function ($ph) {
        $ph.addClass('infoDivPulsate');
        
        setTimeout ( function(){
            $ph.removeClass('infoDivPulsate')
        },2500);
        
        setTimeout(function(){
            myself.pulsate($ph);
        }, spec.pulsatePeriod || 5000);
        
    }
    
    return myself;
};

marketplace.getRegistry().registerEntity('components', wd.marketplace.components.infoDiv({
    name: 'restart',
    description: 'So that changes take effect, please restart the server now',
    pulsatePeriod: 5000
}));




wd.marketplace.components.label = function(spec){
    
    /**
     * Specific specs
     */
    
    var _spec = {
        name: "label",
        description: "Label",
        cssClass: "label",       
        clickAction: undefined
    }; 
    
    
    spec = $.extend({},_spec,spec);
    //var myself = wd.caf.panel(spec);
    var myself = wd.caf.component(spec);
    
    
    myself.label = spec.label || function(){
        
        return "label";
    }
    
    myself.draw = function($ph){
        
        var $c = $("<div/>").addClass(spec.cssClass)
        .text(typeof myself.label==="function"?myself.label():myself.label)
        
       
        if(typeof spec.clickAction === "function"){
            $c.addClass("cafPointer").click(spec.clickAction);
        }
        
        $c.appendTo($ph);
        
    }
    
    return myself;
};


wd.marketplace.components.plugin = function(spec){
    
    /**
     * Specific specs
     */
    
    var _spec = {
        name: "plugin",
        description: "Plugin",
        cssClass: "plugin",       
        clickAction: undefined
    }; 
    
    
    spec = $.extend({},_spec,spec);
    var myself = wd.caf.component(spec);

    var pluginInfo, panel, pluginHeader, pluginBody, isShown=false;
    
    var installationStatus = {
        
        UPTODATE:{
            description: "Up to date",
            cssClass: "pluginStatusUpToDate"
        },
        UPDATEAVAILABLE:{   
            description: "Update available",
            cssClass: "pluginStatusUpdateAvailable"
        },
        INSTALLED:{
            description: "Installed",
            cssClass: "pluginStatusInstalled"
        },
        NOTINSTALLED: {
            description: "Not installed",
            cssClass: "pluginStatusNotInstalled"
        },
        INSTALLING:{
            description: "Installing",
            cssClass: "pluginStatusInstalling"
        },
        UNINSTALLING:{
            description: "Uninstalling",
            cssClass: "pluginStatusUninstalling"
        }
    }
    
    
    // containers
    var $wrapper;
    
    
    myself.setPluginInfo = function(_pluginInfo){
        pluginInfo = _pluginInfo;
    }
    
    
    myself.getPluginInfo = function(){
        return pluginInfo;
    }
  
    myself.setPanel = function(_panel){
        panel = _panel;
    }

    myself.getPanel = function(){
        return panel;
    }

    
    myself.draw = function($ph){
        
        // Wrapper
        
        $wrapper = $("<div/>").addClass(spec.cssClass + " pluginWrapper clearfix")
        .data("plugin",myself) // store it for convenience;

        // Plugin header
        pluginHeader = wd.marketplace.components.pluginHeader({
            clickAction: function(){

                $wrapper.find('.pluginBody .pluginBodyContainer').show();
                $wrapper.find('.pluginHeader').hide();
                				
				//hide other plugins
				$.each(myself.getPanel().getPlaceholder().find('.pluginWrapper'), function(){
					if(!$(this).is($wrapper)) $(this).hide();
				});
            },
            installAction: function(branch){
                panel.installPlugin(myself,branch);
            },
            updateAction: function(branch){
                panel.updatePlugin(myself,branch);
            }
        });
        pluginHeader.init(myself.caf);
        
        pluginHeader.setPlugin(myself);
        pluginHeader.draw($wrapper);
        
        // pluginBody
        pluginBody = wd.marketplace.components.pluginBody({
            installAction: function(branch){
                panel.installPlugin(myself,branch);
            },
            updateAction: function(branch){
                panel.updatePlugin(myself,branch);
            },
            uninstallAction: function(){
                panel.uninstallPlugin(myself);
            }
        });
        pluginBody.init(myself.caf);
        pluginBody.setPlugin(myself);
        pluginBody.draw($wrapper);
        

        $wrapper.appendTo($ph);
        
    }


    myself.isShown = function(){
        return isShown;
    }

    myself.hide = function(){
        
        if(!myself.isShown()){
            return; // already hidden
        }
        
        pluginHeader.unselect();
        pluginBody.hide()
        isShown = false;
    }
    
    
    myself.show = function(){

        pluginHeader.select();
        if(myself.isShown()){
            return; // already shown
        }

        pluginBody.show()
        isShown = true;
    }

    myself.toggleVisibility = function(){
        
        isShown?myself.hide():myself.show();
        
    }
    
    //returns main plugin context excluding versions
    myself.getPluginMainContent = function(){
    	var version = {
        	//creator info
        	company: pluginInfo.company,
        	companyLogo: pluginInfo.companyLogo,
        	companyUrl: pluginInfo.companyUrl,
        	
        	//plugin info
        	name: pluginInfo.name,
        	description: pluginInfo.description,
        	dependencies: pluginInfo.dependencies,
        	license: pluginInfo.license,
        	img: pluginInfo.img,
        	smallImg: pluginInfo.smallImg,
        	screenshots: pluginInfo.screenshots,
        	
        	installed: pluginInfo.installed
        };
        
        return version;
    }
    
    //returns main plugin context excluding versions
    myself.getPluginVersions = function(){
    	return pluginInfo.versions;
    }
    
    //returns installed version main content
    myself.getInstalledVersion = function(){

        if(!pluginInfo.installed){
            return null;
        }

        // Directly return an object that shows the installed version, filling in the details
        // digest the current json and return only the needed info.
        var version = {      	
        	//installed version info
        	branch: pluginInfo.installedBranch,
        	buildId: pluginInfo.installedBuildId,
        	version: pluginInfo.installedVersion
        };
        
        
        // Try to find the name
        pluginInfo && $.isArray(pluginInfo.versions) && pluginInfo.versions.map(function(v){
            if(v.branch == version.branch){
                version.changeLog = v.changeLog;
                version.versionDescription = v.description;
                version.releaseDate = v.releaseDate;
                return false;
            }
        });
        
        // if not found, default to branch id
        if(!version.name){
            version.name = version.installedBranch;
        }
        
        return version;
        
    }

    myself.getDefaultVersion = function(){
        var version = pluginInfo.versions[0]; //TODO: Default is not necessarily the first on the list
        
        return version;
        
    }
        
    
    myself.getInstallationStatus = function(){
        
        // We'll compare the branch / version we have with the version in the same branch
        var installedVersion = myself.getInstalledVersion();
        
        // If nothing is installed, I can mark it as available TODO: implement other state
        if(!installedVersion){
            return installationStatus["NOTINSTALLED"];
        }
        
        
        
        // No versions available? 
        if (!myself.getPluginVersions() || myself.getPluginVersions().length == 0){
            return installationStatus["INSTALLED"];
        }
        
        // loop through the others
        var isUpdateAvailable;
        myself.getPluginVersions().map(function(v){
            if(v.branch == installedVersion.branch){
                
                if(v.version != installedVersion.version){
                    isUpdateAvailable = true;    
                }
                else{
                    // do we have buildId?
                    if(v.buildId){
                        isUpdateAvailable = v.buildId != installedVersion.buildId;
                    }
                    else{
                        isUpdateAvailable = false;
                    }
                }
                return false;
            }
        })
        
        return installationStatus[isUpdateAvailable?"UPDATEAVAILABLE":"UPTODATE"];
        
        
    };
    

    return myself;
};


wd.marketplace.components.pluginHeader = function(spec){
    
    /**
     * Specific specs
     */
    
    var _spec = {
        name: "pluginHeader",
        description: "Plugin Header",
        cssClass: "pluginHeader",
        clickAction: undefined,
        installAction: undefined,
        updateAction: undefined
    }; 
    
    
    spec = $.extend({},_spec,spec);
    var myself = wd.caf.component(spec);

    // BlueprintMixin
    wd.caf.modules.blueprintPanelModule(myself);

    var plugin;

    // Containers
    var $wrapper, $buttonWrapper;
    
    myself.setPlugin = function(_plugin){
        plugin = _plugin;
    }
    
    
    myself.getPlugin = function(){
        return plugin;
    }
    
    
    myself.getInstallationStatus = function(){
        
        return plugin.getInstallationStatus();
        
    }
    
    myself.draw = function($ph){
        
        // Wrapper
        
        $wrapper = myself.generateBlueprintStructure().addClass(spec.cssClass).appendTo($ph);
        myself.update();

        
    }
    
    
    myself.update = function(){
        var installationStatus = plugin.getInstallationStatus();

        $wrapper.empty();

        $("<div/>").addClass("pluginHeaderTitleWrapper ")
          .append($("<div class='leftSide'/>")
          	.append($("<div/>").addClass("pluginHeaderTitle").text(plugin.getPluginInfo().name ) )
          	.append($("<div/>").addClass("pluginHeaderVersion")
                .append($("<span/>").addClass("createdBy").text(plugin.getPluginInfo().company ))
                .append(installationStatus.description == "Not installed" ? $() : $("<span/>").addClass("installedVersion").text("Installed Version: ") )
                .append(installationStatus.description == "Not installed" ? $() : $("<span/>").text(plugin.getPluginInfo().installedVersion + ' (' + plugin.getPluginInfo().installedBranch + ')' ) ) )
          ).append($("<div class='centerSide'/>")
          ).append($("<div class='rightSide'/>")
          	.append($("<div/>").text("View details").addClass("viewDetailsDesc") )
          	.append($("<div/>").addClass("viewDetailsImage") )
          ).addClass(installationStatus.description == "Not installed" ? "" : "installed")
        .appendTo($wrapper);
        
            
        var $buttonWrapper = $("<div/>").addClass("pluginHeaderButtonWrapper ").addClass('pluginHeaderVersionWrapper') //TODO: remove this last one
        .appendTo($wrapper);
        

        if(installationStatus.description == "Not installed"){
         	wd.marketplace.components.pluginButton({
            	cssClass: "installButton",
            	label: "Install",
            	clickAction: function(){ 
            		var v = plugin.getDefaultVersion();
                	spec.installAction(v.branch);
            	},
            	image: "img/install_button_icon.png"
        	}).draw($buttonWrapper);        	
        }
        else if(installationStatus.description == "Update available"){
        	wd.marketplace.components.pluginButton({
        		cssClass: "updateButton",
            	label: "Upgrade",
            	clickAction: function(){ 
            	    var v = plugin.getInstalledVersion();
            	    spec.updateAction( v.branch ) ;
            	},
            	image: "img/update_button_icon.png"
        	}).draw($buttonWrapper);
        } 
        else if(installationStatus.description == "Up to date"){
        	wd.marketplace.components.pluginButton({
        		cssClass: "uptodateButton",
            	label: "Up to Date",
            	image: "img/uptodate_button_icon.png",
            	clickAction: function(){
            	}
        	}).draw($buttonWrapper);
        }

        if(typeof spec.clickAction === "function"){
            $wrapper.find('.pluginHeaderTitleWrapper').click(spec.clickAction);
        }

        
    }
    
    myself.select = function(){
        $wrapper.addClass("pluginSelected");
    }

    myself.unselect = function(){
        $wrapper.removeClass("pluginSelected");
    }
    
    return myself;

}

wd.marketplace.components.pluginBody = function(spec){
    
    /**
     * Specific specs
     */
    
    var _spec = {
        name: "pluginBody",
        description: "Plugin Body",
        cssClass: "pluginBody",
        installAction: undefined,
        updateAction: undefined,
        uninstallAction: undefined
    }; 
    
    
    spec = $.extend({},_spec,spec);
    var myself = wd.caf.component(spec);

    // BlueprintMixin
    wd.caf.modules.blueprintPanelModule(myself);

  

    var plugin, selectedVersion;

    // Containers
    var $element, $wrapper, 
        $pluginBodyTop, $pluginBodyDetailsArea, $pluginBodyBottom;

    // Top Elements
    var $closeButtonObj;
    
    // Details Area   
    var pluginVersionDetails;

    // Bottom Elements
    var $versionSelectorObj, $installButtonObj, $updateButtonObj, $uninstallButtonObj;



    myself.setPlugin = function(_plugin){
        plugin = _plugin;
    }
    
    
    myself.getPlugin = function(){
        return plugin;
    }

    
    myself.draw = function($ph){
        
        // Create a full wrapper and one for the animation
        $element = myself.generateBlueprintStructure().addClass(spec.cssClass).appendTo($ph);
        $wrapper = $("<div/>").addClass("pluginBodyContainer").appendTo($element);
        
        // On draw, this will be collapsed
  // TODO: modify this when details area is final, to
       
        var mainPluginContent = plugin.getPluginMainContent(),
        	version = ( mainPluginContent.installed) ? plugin.getInstalledVersion() : plugin.getDefaultVersion(),
        	v = $.extend({},mainPluginContent,version);
        
        
        myself.selectVersion(v); 
        myself.update();

    }
    
    
    myself.update = function(){
        var installationStatus = plugin.getInstallationStatus();
        
        $wrapper.empty();
        
        $top = $("<div/>").addClass("pluginBodyContainerTop").appendTo($wrapper);
        
        // Top title and close buttom
        $pluginBodyTop = $("<div/>").addClass("pluginBodyTop clearfix").appendTo($top);
        
        var pluginInfo = plugin.getPluginInfo();

         $("<div/>").addClass("pluginHeaderTitleWrapper")
          .append($("<div/>").addClass("pluginHeaderTitle").text(pluginInfo.name ) )
          .append($("<div/>").addClass("pluginHeaderVersion").text( installationStatus.description == "Not installed" ? null : plugin.getPluginInfo().installedVersion + ' (' + plugin.getPluginInfo().installedBranch + ')' ) )
        .appendTo($pluginBodyTop);

        var $closeButtonObj = $("<div/>").addClass("pluginHeaderButtonWrapper ").addClass('pluginHeaderVersionWrapper') //TODO: remove this last one when css is working
        .appendTo($pluginBodyTop);

        wd.marketplace.components.pluginButton({
                    cssClass: "closeButton",
                    label: "Close",
                    clickAction: function () {
						$wrapper.hide();
						
						//show all headers
						var $ph = plugin.getPanel().getPlaceholder();
						
						$.each($ph.find('.pluginHeader'),function(i, header){
							$(header).show();
						});
						
						//hide other plugins
						$.each($ph.find('.pluginWrapper'), function(){
							if(!$(this).is($wrapper)) $(this).show();
						});
                    },
                    image: "img/close.png"
        }).draw($closeButtonObj);
        

        // Wrapper for detailsArea
        $pluginBodyDetailsArea = $("<div/>").addClass("pluginBodyDetailsArea clearfix").appendTo($top);

        pluginVersionDetails = wd.marketplace.components.pluginVersionDetails({});
        pluginVersionDetails.setPluginVersion(selectedVersion);
        pluginVersionDetails.draw($pluginBodyDetailsArea);

        // Add footer
        
        $bottom = $("<div/>").addClass("pluginBodyContainerBottom").appendTo($wrapper);
        
        $pluginBodyBottom = $("<div/>").addClass("pluginBodyBottom clearfix").appendTo($bottom);
        $versionSelectorObj =  $("<div/>").addClass("pluginVersionSelectorContainer span-6").appendTo($pluginBodyBottom);
        $installButtonObj =  $("<div/>").addClass("pluginButton span-4").appendTo($pluginBodyBottom);
        $updateButtonObj =  $("<div/>").addClass("pluginButton span-4").appendTo($pluginBodyBottom);
        $uninstallButtonObj =  $("<div/>").addClass("pluginButton span-4").appendTo($pluginBodyBottom);
        

        if(plugin.getPluginInfo().versions){
            var selector = wd.marketplace.components.pluginVersionSelector({
                cssClass: "pluginVersionSelector",
                availableVersions: plugin.getPluginInfo().versions ,
                changeAction: myself.selectVersion
            });
            selector.setPluginVersion(selectedVersion);
            selector.draw($versionSelectorObj);
        }

        wd.marketplace.components.pluginButton({
            cssClass: "installButton",
            label: "Install",
            clickAction: function(){ 
                spec.installAction(selectedVersion.branch);
            },
            image: "img/install_button_icon.png"
        }).draw($installButtonObj);

        wd.marketplace.components.pluginButton({
        	cssClass: "updateButton",
            label: "Upgrade",
            clickAction: function(){ 
                var v = plugin.getInstalledVersion();
                spec.updateAction( v.branch ) ;
            },
            image: "img/update_button_icon.png"
        }).draw($updateButtonObj);

        wd.marketplace.components.pluginButton({
            cssClass: "uninstallButton",
            label: "Uninstall",
            clickAction: spec.uninstallAction,
            image: "img/uninstall_button_icon.png"
            
        }).draw($uninstallButtonObj);

		//set buttons depending installationStatus
		if(installationStatus.description == "Update available"){
			$installButtonObj.find('button').attr('disabled','disabled').addClass('disabled');
		} else if(installationStatus.description == "Not installed"){
			$updateButtonObj.find('button').attr('disabled','disabled').addClass('disabled');
			$uninstallButtonObj.find('button').attr('disabled','disabled').addClass('disabled');
		} else if(installationStatus.description == "Up to date"){
			$installButtonObj.find('button').attr('disabled','disabled').addClass('disabled');
			$updateButtonObj.find('button').attr('disabled','disabled').addClass('disabled');
		}
		
		
		$wrapper.hide();
        
    }
    
    
    myself.selectVersion = function(version){
        
        myself.log("Selecting branch: " + version.branch);
        
        selectedVersion = version;  
           
        if(pluginVersionDetails != undefined) {
        	pluginVersionDetails.setPluginVersion(selectedVersion);   
        	pluginVersionDetails.update();
        }
        
        if($installButtonObj != undefined && $updateButtonObj != undefined && $uninstallButtonObj != undefined){
        	var installedVersion = plugin.getInstalledVersion();
        	var installationStatus = plugin.getInstallationStatus();
        	
        	//set buttons depending installationStatus
			if(installedVersion.branch != selectedVersion.branch){
				$installButtonObj.find('button').removeAttr('disabled').removeClass('disabled');
				$updateButtonObj.find('button').attr('disabled','disabled').addClass('disabled');
				$uninstallButtonObj.find('button').attr('disabled','disabled').addClass('disabled');
			} else if(installationStatus.description == "Update available"){
				$installButtonObj.find('button').attr('disabled','disabled').addClass('disabled');
				$updateButtonObj.find('button').removeAttr('disabled').removeClass('disabled');
				$uninstallButtonObj.find('button').removeAttr('disabled').removeClass('disabled');
			} else if(installationStatus.description == "Not installed"){
				$installButtonObj.find('button').removeAttr('disabled').removeClass('disabled');
				$updateButtonObj.find('button').attr('disabled','disabled').addClass('disabled');
				$uninstallButtonObj.find('button').attr('disabled','disabled').addClass('disabled');
			}
		}                	
    }
    
    myself.showBodyDesc = function(){   
        myself.log("showBodyDesc");
    }
    
    
    /*myself.hide = function(){
        
        if(Modernizr.csstransitions){
            $wrapper.css("margin-top","-5000px").addClass("marketplaceTransparent");
        }
        else{
            $element.hide();
        }
        
    }
    
    
    myself.show = function(){

        // Always force body desc to show
        myself.showBodyDesc();
        
        if(Modernizr.csstransitions){  
            $wrapper.css("margin-top","0px").removeClass("marketplaceTransparent");
        }
        else{
            $element.show();
        }        
        

    }
*/

    return myself;

}

wd.marketplace.components.pluginVersionDetails = function(spec){
    
    //
    // Specific specs
    //
    
    var _spec = {
        name: "pluginVersionDetails",
        description: "Plugin Version Details",
        cssClass: "pluginVersionDetails"
    }; 
    spec = $.extend({},_spec,spec);
    var myself = wd.caf.component(spec);
    
    var propertyMappingLeft = {
        version:{
            label: "Version"
        },
        company:{
           label: "Created by" 
        },
        license: {
            label: "License"
        },
        dependencies:{
            label: "Dependencies"
        },
        releaseDate:{
            label: "Release Date"
        }
    }
    
    var propertyMappingRight = {
    	description:{
            label: "Description"
        },
        versionDescription:{
        },
        screenshots:{
        	label: "Screenshots"
        }
    }

    var pluginVersion;
    
    myself.getPluginVersion = function(){
    	return pluginVersion;
    }
    
    myself.setPluginVersion = function(_pluginVersion){
    	pluginVersion = _pluginVersion;
    }

    myself.draw = function($ph){
        
        myself.setPlaceholder($ph);
        myself.update();
        
    }
    
    
    
    myself.update = function(){
        
        var $ph = myself.getPlaceholder().empty(); 
        


        //TODO: Temp. This 'if' might not be necessary

        if (pluginVersion ){
        
        	//build left side
        	var $leftSide = $("<div/>");
        	$leftSide.addClass("pluginVersionLeft");
        	$ph.append($leftSide);
        	
    		$("<img/>").attr('src',pluginVersion.img).appendTo($leftSide); 
    		$("<div style='height: 25px'> <div class='infoIcon'/><div class='desc'>INFO</div></div>").appendTo($leftSide);   
        	
        	$.each(propertyMappingLeft, function(prop){
        		if(pluginVersion[prop] != undefined){
        			var label = $("<div/>").addClass("clearfix pluginVersionPropLabel"),
        				value = $("<div/>").addClass("clearfix pluginVersionPropValue");
        			
                    label.text(propertyMappingLeft[prop].label);
        			if(prop == "company"){
                        value.append($("<a href='"+pluginVersion["companyUrl"]+"' target='_blank'>"+pluginVersion[prop]+"</a>"));
                    } else {
                        value.text(pluginVersion[prop]);
                    }
        		
        			$leftSide.append(label);
        			$leftSide.append(value);
        		}
        	});
        	
        	//build right side
        	var $rightSide = $("<div/>");
        	$rightSide.addClass("pluginVersionRight description last");
        	$ph.append($rightSide);
        	
        	
        	//append description
        	$("<div style='height: 25px; border-bottom: solid 1px #CCC'><div class='descriptionIcon'/><div class='desc'>DESCRIPTION</div> </div>").appendTo($rightSide); 
        	$("<div/>").addClass("pluginVersionDescription").html((pluginVersion.description!=null?pluginVersion.description:"") + " <br/> <br/> " + (pluginVersion.versionDescription != null? pluginVersion.versionDescription : "")).appendTo($rightSide);
        	
        	//only input screenshots if it is available
        	if(pluginVersion.screenshots != null){
        		var $rightSideSlideshow = $("<div/>");
        		$rightSideSlideshow.addClass("pluginVersionRight slideshow last");
        		$ph.append($rightSideSlideshow);
        	
        		//append screenshots
        		$("<div style='height: 25px; border-bottom: solid 1px #CCC'><div class='screenshotsIcon'/><div class='desc'>SCREENSHOTS</div> </div>").appendTo($rightSide); 
        		var $screenshots = $("<div/>").addClass("screenshots"),
        			$screenshotHolder = $("<div/>").addClass("screenshotsHolder");
        		
        		$screenshotHolder.appendTo($screenshots);
        		
        		var $slideshow = $("<div/>").addClass("slider-wrapper theme-default"),
        			$slideshowHolder = $("<div id='slider'/>").addClass("nivoSlider");
        		
        		$slideshowHolder.appendTo($slideshow);
        	
        	    var slidesProperties = {
                    effect: 'fade',
                    directionNav: true,
                    startSlide: 0,
                    randomStart: false,
                    manualAdvance: true,
                    animSpeed: 300
                };

        		$.each(pluginVersion.screenshots, function(i){
        			//screenshot
        			var $imgPh = $("<div/>").addClass('image');
        			$("<img/>").attr('src',pluginVersion.screenshots[i])
        					   .attr('height',75)
        					   .attr('width',140)
        					   .addClass("imageBorder")
        					   .click(function(e){
		   		
                                    $rightSideSlideshow.css('display','inline-block');
                                    $rightSide.css('display','none');

                                    var paginationButton = $rightSideSlideshow.find('.nivo-controlNav a[rel="'+i+'"]');
                                    paginationButton.trigger('click');
                                    
                                    $(".pluginVersionRight").bind("click",function(e){e.stopPropagation();});
                                    $("body").bind("click.bodyClick",function(e){
                                        $rightSideSlideshow.css('display','none');
                                        $rightSide.css('display','inline-block');

                                        $("body").unbind("click.bodyClick");
                                    });

                                    e.stopPropagation();
        					   		
        					   }).appendTo($imgPh);   
        			$screenshotHolder.append($imgPh);  
        		
        			//slideshow
        			$imgPh = $("<div/>");
        			$("<img/>").attr('src',pluginVersion.screenshots[i]).attr('height',300).attr('width',560).attr('data-thumb',pluginVersion.screenshots[i]).appendTo($imgPh);   
        			$slideshowHolder.append($imgPh);  
        		}); 
        		
        		$screenshotHolder.css('width',pluginVersion.screenshots.length*160+'px');
        	
        		$screenshots.appendTo($rightSide);
        	
	        	$slideshow.appendTo($rightSideSlideshow);
   	     	
				$slideshowHolder.nivoSlider(slidesProperties);
                
                
				var $closeSlideshowButton = $("<div/>").addClass("closeSlideshowButton");//.prependTo($rightSideSlideshow);

                $("<div/>").addClass('closeButtonHolder').append($closeSlideshowButton).prependTo($rightSideSlideshow);
				//$closeSlideshowButton.append("<div class='image'></div>").append("<div class='text'>close</div>");
	
				$closeSlideshowButton.click(function(){
					$rightSideSlideshow.css('display','none');
                    $rightSide.css('display','inline-block');
				});
				

				var $prev = $rightSideSlideshow.find('.prev'),
					$next = $rightSideSlideshow.find('.next');
				
				$prev.empty();
				$prev.append("<div class='image'/>");
				$prev.append("<div class='text'>previous</div>");
				$prev.detach();
				
				$next.empty();
				$next.append("<div class='image'/>");
				$next.append("<div class='text'>next</div>");
				$next.detach();
				
				$next.appendTo($slideshow);
				$prev.appendTo($slideshow);
				
				$rightSideSlideshow.css('display','none');
				}
        	}
        
    }
           
    return myself;
}



wd.marketplace.components.pluginVersionSelector = function(spec){
    
    /**
     * Specific specs
     */
    
    var _spec = {
        name: "pluginVersionSelector",
        description: "Plugin Version Selector",
        cssClass: "pluginVersionSelector",
        availableVersions: undefined,
        changeAction: undefined
    }; 
    
    
    spec = $.extend({},_spec,spec);
    var myself = wd.caf.component(spec);

    var pluginVersion, versionMap = {};

    var $ph, $pluginVersionSelector;


    myself.draw = function($ph){
        myself.setPlaceholder( $ph );

        $.each ( spec.availableVersions , function (idx, el ){
          versionMap[el.branch] = el;  
        });

        myself.update();
    }
    
    
    
    myself.update = function(){
        
        var $ph = myself.getPlaceholder().empty();        

        
        $pluginVersionSelector = $("<select/>").addClass(spec.cssClass + " chzn-select").appendTo($ph);
        $.each( spec.availableVersions , function (idx, el) {
        	var $option = $('<option/>').val(el.branch).text(el.version + " (" + el.name + ")");
        	if(el.branch == pluginVersion.branch){
        		$option.attr('selected','selected');
        	}
        	$option.appendTo($pluginVersionSelector);
        });
        $pluginVersionSelector.change( function () {
        	//get only relevant properties
        	var version = versionMap[$pluginVersionSelector.val()];
        	
        	var newVersion = {
        		branch: version.branch,
        		buildId: version.buildId,
        		version: version.version,
                changeLog: version.changeLog,
                versionDescription: version.description,
                releaseDate: version.releaseDate
        	}
        	
            myself.setPluginVersion( newVersion );
            myself.changeAction();     
        });
        $pluginVersionSelector.select2();
    }
    
    
    myself.setPluginVersion = function(_pluginVersion){
        
        pluginVersion = $.extend({},pluginVersion, _pluginVersion);
        
    }
    
    myself.changeAction = function(){
        
        spec.changeAction(pluginVersion);
        
    }
    
    return myself;
}



wd.marketplace.components.pluginButton = function(spec){
    
    /**
     * Specific specs
     */
    
    var _spec = {
        name: "pluginButton",
        description: "Plugin Button",
        cssClass: "pluginButton",
        clickAction: undefined,
        label: "Button"
    }; 
    
    
    spec = $.extend({},_spec,spec);
    var myself = wd.caf.component(spec);

    var $button;

    myself.draw = function($ph){
        myself.setPlaceholder( $ph );
        myself.update();
    }
    
    
    
    myself.update = function(){

        var $ph = myself.getPlaceholder().empty();        

        // version selector
        $button = $("<button/>").addClass(spec.cssClass);
        
        if(spec.image != undefined){
        	$button.append("<span style='line-height: 25px; padding-left: 20px;'>"+spec.label+"</span>");
        	$button.append("<div class='icon'></div>");
        } else {
        	$button.text(spec.label);
        }
        
        $button.appendTo($ph);
        $button.click( myself.clickAction);     
    }
    
        
    myself.clickAction = function(){
        
        spec.clickAction();
        
    }
    
    return myself;
}



/*
 *
 *  Popups
 *
 */ 


wd.marketplace.popups.basicPopup = function(spec){


    /**
     * Specific specs
     */
    var _spec = {
        name: "basic",
        description: "Basic Popup"        
    };

    /* Specific options for showing */
    var _options = {        
        content: "Content", 
        status: "Status",
        details: "details",
        bottom: "bottom",
        img: "",
        cssClass:"",
        buttons: []
    };


    spec = $.extend({},_spec,spec);
    var options = $.extend({},_options,options);
    var myself = wd.caf.impl.popups.basicPopup(spec);

    /**
     * Draws the content
     * @name basicPopup.drawContent
     * @memberof wd.caf.impl.popups.basicPopup
     * @param Object to control this popups' behavior
     */
    myself.drawContent = spec.drawContent || function(options){

        // Write: topPoup, contentPopup, bottomPopup
        
        var $ph = $("<div/>").addClass("basicPopupContainer " + " " + options.cssClass);
        
        $centerContainer = $("<div/>").addClass("cafBasicPopupCenterContainer").appendTo($ph)
        .append( $('<div>&nbsp;</div>').addClass('popupImgContainer') )
        .append( myself.drawCenterContent(options) );
        $bottomContainer = $("<div/>").addClass("cafBasicPopupBottomContainer").appendTo($ph)
        .append(myself.drawBottomContent(options).prepend(options.bottom) );

        myself.log("here");
        return $ph;
    }


    /**
     * Draws the center content
     * @name basicPopup.drawTopContent
     * @memberof wd.caf.impl.popups.basicPopup
     */
    myself.drawCenterContent = spec.drawCenterContent || function(options){
    	var $container = $('<div/>').addClass('popupInfoContainer'),
    		$status = $('<div/>').addClass('popupStatus').append(
            (typeof options.status === "function") ? options.status(myself,options) : options.status),
    		$content = $('<div/>').addClass('popupContent').append(
            (typeof options.content === "function") ? options.content(myself,options) : options.content),
    		$details = $('<div/>').addClass('popupDetails').append(
            (typeof options.details === "function") ? options.details(myself,options) : options.details);
        
        var status = true, content = true, details = true;
    	
    	if(options.status == undefined || options.status == _options.status){
    		status = false;
    	}
    	if(options.content == undefined || options.content == _options.content){
    		content = false;
    	}
    	if(options.details == undefined || options.details == _options.content){
    		details = false;
    	}
    	
    	if(status){
    		if(!details) $status.addClass('noDetails');
    		if(!content) $status.addClass('noContent');
    		$container.append($status);
    	}
    	if(content){
    		if(!status) $container.addClass('noStatus');
    		if(!details) $container.addClass('noDetails');
    		$container.append($content);
    	}
    	if(details){
    		if(!status) $details.addClass('noStatus');
    		if(!content) $details.addClass('noContent');
    		$container.append($details);
    	}
    	
    	
    	return $container;
        
        /*return $('<div/>').addClass('popupInfoContainer')
        .append( $('<div/>').addClass('popupStatus').append(
            (typeof options.status === "function") ? options.status(myself,options) : options.status) )
        .append( $('<div/>').addClass('popupContent').append(
            (typeof options.content === "function") ? options.content(myself,options) : options.content) )
        .append( $('<div/>').addClass('popupDetails').append(
            (typeof options.details === "function") ? options.details(myself,options) : options.details) );*/
            
    }


    /**
     * Shows the popup
     * @name basicPopup.show
     * @memberof wd.caf.impl.popups.basicPopup
     * @param Object to control this popups' behavior. eg: {content: "Test",buttons:[...]}
     */
    myself.show = spec.show || function(options){
        
        options = $.extend({},_options,options);
        
        myself.log("Popup show action");
        myself.caf.popupEngine.show(myself,options);
        
    }
    
    return myself;
}

marketplace.getRegistry().registerPopup( wd.marketplace.popups.basicPopup() );


wd.marketplace.popups.closePopup = function(spec){


    /**
     * Specific specs
     */
    var _spec = {
        name: "close",
        description: "Close Popup"        
    };

    /* Specific options for showing */
    var _options = {        
        content: "Content", 
        status: "Status",
        details: "details",
        bottom: "bottom",
        img: "", 
        validateFunction: undefined,
        cssClass:"",
        buttons:[{
            label: "OK",
            validate: false,
            cssClass: "popupClose", 
            callback: function(popup,options){
                myself.hide()
            },
            validateFunction: undefined
        }
        ]
    }


    spec = $.extend({},_spec,spec);
    var options  = $.extend({},_options,options);
    var myself = wd.marketplace.popups.basicPopup(spec);
    
    
    /**
     * Shows the popup
     * @name basicPopup.show
     * @memberof wd.caf.impl.popups.basicPopup
     * @param Object to control this popups' behavior. eg: {content: "Test",buttons:[...]}
     */
    myself.show = spec.show || function(options){
        
        options = $.extend({},_options,options);
        
        myself.log("Popup show action");
        myself.caf.popupEngine.show(myself,options);
        
    }
    
    return myself;
}


marketplace.getRegistry().registerPopup( wd.marketplace.popups.closePopup() );



wd.marketplace.popups.okcancelPopup = function(spec){


    /**
     * Specific specs
     */
    var _spec = {
        name: "okcancel",
        description: "Ok/Cancel Popup"        
    };

    /* Specific options for showing */
    var _options = {        
        content: "Content", 
        status: "Status",
        details: "details",
        bottom: "bottom",
        img: "",
        cssClass:"",
        validateFunction: undefined,
        okCallback: function(){
            myself.log("Action not defined","warn")
        },
        buttons:[
        {
            label: "Ok",
            validate: true,
            cssClass: "popupClose", 
            callback: function(popup,options){
                // Call function. if return true, hide.
                if(options.okCallback(popup,options)){
                    myself.hide();
                }
            },
            validateFunction: undefined
        },
        {
            
            label: "Cancel",
            validate: false,
            cssClass: "popupClose", 
            callback: function(popup,options){
                myself.hide()
            },
            validateFunction: undefined
        }
        ]
    }


    spec = $.extend({},_spec,spec);
    var options  = $.extend({},_options,options);
    var myself = wd.marketplace.popups.basicPopup(spec);
    
    
    /**
     * Shows the popup
     * @name basicPopup.show
     * @memberof wd.caf.impl.popups.basicPopup
     * @param Object to control this popups' behavior. eg: {content: "Test",buttons:[...]}
     */
    myself.show = spec.show || function(options){
        
        options = $.extend({},_options,options);
        
        myself.log("Popup show action");
        myself.caf.popupEngine.show(myself,options);
        
    }
    
    return myself;
}


marketplace.getRegistry().registerPopup( wd.marketplace.popups.okcancelPopup() );



/*
 *
 *  Panels
 *
 */ 

wd.marketplace.panels.marketplacePanel = function(spec){
  
    
    /**
     * Specific specs
     */
    
    var _spec = {
        name: "marketplacePanel",
        description: "Marketplace",
        order: 10,
        color: "red"
    };

    spec = $.extend({},_spec,spec);
    var myself = wd.caf.panel(spec);
    
    
    // Vars
    var INSTALL = "install", UNINSTALL = "uninstall", UPDATE = "update";
    var $panel, $mainContent, plugins = [];
    
    // BlueprintMixin
    wd.caf.modules.blueprintPanelModule(myself);



    // Connecting componnet
    
    var connectingComponent = wd.caf.component(
    {
        name: "connectingComponent",
        description: "Connecting component info",
        cssClass: "connectingComponent",
        draw: function($ph){
            $("<div/>").addClass("connectingComponent").text("Connecting to server...").appendTo($ph);
        }
    
    });
    
    
    var restartInfoComponent; 
      
      
    /**
     * Describes this interface
     * @name panel.init
     * @memberof wd.caf.panel
     */
    myself.draw = spec.draw || function($ph){
        $("<div/>").addClass("marketplacePanelHeader").appendTo($ph);
        
        //$panel = myself.generateBlueprintStructure().appendTo($ph);
        $panel = $("<div/>").appendTo($ph);

        
        
        $mainContent = $('<div/>').addClass("marketplacePanel");
        $panel.append($mainContent);
        
        
        
    }


    myself.cleanPlugins = function(){
        
        plugins = [];
        $mainContent.empty();
    
    }
    
    
    myself.getPlugins = function(){
        return plugins;
    }


    myself.showConnectingComponent = function(){
        
        connectingComponent.draw($mainContent.empty());
        
    }


    myself.addPlugin = function(pluginInfo){
        
        var plugin = wd.marketplace.components.plugin();
        plugin.init(myself.caf);
        plugin.setPluginInfo(pluginInfo);
        plugin.setPanel(myself);
        
        // Add it
        plugin.draw($mainContent);
        plugins.push(plugin);
        
    }
    
    
    myself.pluginHeaderClicked = function(plugin){
        
        
        
    }
    
    
    myself.installPlugin = function(plugin, branch){      
        myself.caf.popupEngine.getPopup("okcancel").show({
            status: "Do you want to install now?",
            content: "<span class='pluginName'>"+plugin.getPluginInfo().name+"</span>"+"<br/>"+"<span class='pluginVersion'>"+branch+"</span>",
            details: "",
            bottom: "You are about to start the installation. Do you want to proceed?",
            cssClass: "popupInstall",
            okCallback: function(){
                myself.log("Install plugin " + plugin.getPluginInfo().id + ", Branch: " + branch,"info");
        
                // 1. Set the notification for the installing operation
                myself.startOperation(INSTALL, plugin, branch);

                // 2. Send to engine
                myself.caf.engine.installPlugin(plugin.getPluginInfo().id, branch, function(){
                    myself.log("Install plugin done: " + plugin.getPluginInfo().id +", branch " + branch ,"info")
                    myself.stopOperation(INSTALL, plugin, branch)
                }, function(jqXHR, textStatus){
                    myself.log("Error installing plugin: " + plugin.getPluginInfo().id +", branch " + branch ,"info")
                    myself.errorOperation(INSTALL, plugin, branch,jqXHR.message)
                } );
            },
            validateFunction: function () {
                return true
            }
        });
    }
    
    myself.updatePlugin = function(plugin, branch){      
        myself.caf.popupEngine.getPopup("okcancel").show({
            status: "Do you want to upgrade now?",
            content: "<span class='pluginName'>"+plugin.getPluginInfo().name+"</span>"+"<br/>"+"<span class='pluginVersion'>"+branch+"</span>",
            details: "",
            bottom: "You are about to start the upgrade. Do you want to proceed?",
            cssClass: "popupUpdate",
            okCallback: function(){
                myself.log("Upgrade plugin " + plugin.getPluginInfo().id + ", Branch: " + branch,"info");
        
                // 1. Set the notification for the installing operation
                myself.startOperation(UPDATE, plugin, branch);

                // 2. Send to engine
                myself.caf.engine.installPlugin(plugin.getPluginInfo().id, branch, function(){
                    myself.log("Upgrade plugin done: " + plugin.getPluginInfo().id +", branch " + branch ,"info")
                    myself.stopOperation(UPDATE, plugin, branch)
                }, function(jqXHR, textStatus){
                    myself.log("Error upgrading plugin: " + plugin.getPluginInfo().id +", branch " + branch ,"info")
                    myself.errorOperation(UPDATE, plugin, branch,jqXHR.message)
                } );
            },
            validateFunction: function () {
                return true
            }
        });
    }
    
        
    myself.uninstallPlugin = function(plugin){
        
        myself.caf.popupEngine.getPopup("okcancel").show({
            status: "Do you want to uninstall now?",
            content: plugin.getPluginInfo().name,
            details: "",
            bottom: "You are about to uninstall. Do you want to proceed?",
            cssClass: "popupUninstall",
            okCallback: function(){
                myself.log("Uninstall plugin " + plugin.getPluginInfo().id ,"info");
        
                // 1. Set the notification for the installing operation
                myself.startOperation(UNINSTALL, plugin);
        
                // 2. Send to engine
                myself.caf.engine.uninstallPlugin(plugin.getPluginInfo().id, function(){
                    myself.log("Uninstall plugin done: " + plugin.getPluginInfo().id ,"info");
                    myself.stopOperation(UNINSTALL, plugin); 
                }, function(jqXHR, textStatus){
                    myself.log("Error uninstalling plugin: " + plugin.getPluginInfo().id ,"info");
                    myself.errorOperation(UNINSTALL, plugin, "" ,jqXHR.message); 
                } );
            },
            validateFunction: function () {
                return true
            }
        });
      
    }


    myself.startOperation = function(operation, plugin, branch){
        
        if(operation == INSTALL){
            var popupStatus = "Installing",
        	popupDetails = undefined,
            popupContent = "<span class='pluginName'>"+plugin.getPluginInfo().name+"</span>"+"<br/>"+"<span class='pluginVersion'>"+branch+"</span>",
            cssClass =  "popupInstall popupOperation";
        } else if(operation == UPDATE){
        	var popupStatus = "Upgrading",
        	popupDetails = undefined,
            popupContent = "<span class='pluginName'>"+plugin.getPluginInfo().name+"</span>"+"<br/>"+"<span class='pluginVersion'>"+branch+"</span>",
            cssClass =  "popupInstall popupOperation";
        } else{
            var popupStatus = "Uninstalling",
            popupContent = plugin.getPluginInfo().name,
            cssClass =  "popupUninstall popupOperation";
        }
        myself.log("Starting " + operation + " operation");

        myself.caf.popupEngine.getPopup("basic").show({
            status: popupStatus,
            content: popupContent,
            details: "Please wait...",
            bottom: "",
            cssClass: cssClass
        });

    }
    
    
    myself.stopOperation = function(operation, plugin, branch){

        if(operation == INSTALL){
            var popupStatus = "Successfuly installed ",
            popupContent = undefined,
            popupDetails = "Installed "+plugin.getPluginInfo().name +" with branch "+branch+((plugin.getPluginInfo().installationNotes) ? "<br/>"+plugin.getPluginInfo().installationNotes : "")+"<br/><br/>"+"<span style='font-weight: bold; font-size: 13px'>You must restart your server for changes to take effect</span>",
            cssClass = "popupInstall popupSuccess";
        } else if(operation == UPDATE){
            var popupStatus = "Successfuly upgraded ",
            popupContent = undefined,
            popupDetails = "Upgraded "+plugin.getPluginInfo().name +" with branch "+branch+((plugin.getPluginInfo().installationNotes) ? "<br/>"+plugin.getPluginInfo().installationNotes : "")+"<br/><br/>"+"<span style='font-weight: bold; font-size: 13px'>You must restart your server for changes to take effect</span>",
            cssClass = "popupInstall popupSuccess";    
        } else{
            var popupStatus = "Successfully uninstalled ",
            popupContent = undefined,
            popupDetails = "Uninstalled "+plugin.getPluginInfo().name+"<br/><br/>"+"<span style='font-weight: bold; font-size: 13px'>You must restart your server for changes to take effect</span>",
            cssClass = "popupUninstall popupSuccess";
        }
        myself.log("Stopping " + operation + " operation");

        myself.caf.popupEngine.getPopup("close").show({
            status: popupStatus,
            content: popupContent,
            bottom: "",
            details: popupDetails,
            cssClass: cssClass
        });
        
        myself.caf.actionEngine.getAction('refresh').executeAction();
        
        if (!restartInfoComponent) {
            restartInfoComponent = myself.caf.getRegistry().getEntity('components', 'restart');
        }   
        restartInfoComponent.draw( myself.caf.templateEngine.getTemplate().$toggleActionContainer );
    }
    
    myself.errorOperation = function(operation, plugin, branch, errorMsg){
        
        errorMsg = errorMsg || "";
        if(operation == INSTALL){
            var popupStatus = "Error installing ",
            popupDetails = (errorMsg != "" ? errorMsg + "<br/>": "Please check server logs" + "<br/>"),
            cssClass = "popupInstall popupError";
        } else if(operation == UPDATE){
        	var popupStatus = "Error upgrading ",
            popupDetails = (errorMsg != "" ? errorMsg + "<br/>": "Please check server logs" + "<br/>"),
            cssClass = "popupInstall popupError";
        } else{
            var popupStatus = "Error uninstalling ",
            popupDetails = (errorMsg != "" ? errorMsg + "<br/>": "Please check server logs" + "<br/>"),
            cssClass = "popupUninstall popupError";
        }
        myself.log("Starting " + operation + " operation");

        myself.caf.popupEngine.getPopup("close").show({
            status: popupStatus,
            content: undefined,
            bottom: "",
            details: popupDetails,
            cssClass: cssClass
        });
        
        myself.caf.actionEngine.getAction('refresh').executeAction();
        
    }
    
    
    return myself;
    
    
};







// Register the panels

marketplace.getRegistry().registerPanel(wd.marketplace.panels.marketplacePanel({
    name:"installedPluginsPanel",
    description:"Installed plugins",
    order: 20
}));


marketplace.getRegistry().registerPanel(wd.marketplace.panels.marketplacePanel({
    name:"allPluginsPanel",
    description:"All plugins",
    order: 10
}));

/*
marketplace.getRegistry().registerPanel(wd.caf.impl.panels.underConstruction({
    name:"about",
    description:"About",
    order: 90
}));*/




$(function(){
    marketplace.init();
    marketplace.engine.init();
})


