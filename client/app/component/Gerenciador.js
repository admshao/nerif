var fs = require('fs');

Ext.define('Nerif.component.Gerenciador', {
	extend: 'Ext.Component',

	singleton: true,
	alternateClassName: ['Gerenciador'],

	server: '',
	logDirectory: '',
	logProperties: [],
	executionTime: '',
	
	indicatorsModule: true,
	statisticsModule: true,
	analysisModule: true,	
	
	users: [],
	indicators: [],
	groups: [],

	clear: function() {
		var obj = this;
		
		obj.server = '';
		obj.logDirectory = '';
		obj.logProperties = [];
		
		obj.users = [];
		obj.indicators = [];
		obj.groups = [];
	},
	
	initComponent: function() {
		var obj = this;

		if (fs.existsSync('./config/config.json')) {
			var data = fs.readFileSync('./config/config.json').toString();
			var jsonObj = Ext.decode(data);
			
			obj.server = jsonObj.server;
			obj.logDirectory = jsonObj.logDirectory;
			obj.logProperties = jsonObj.logProperties;
			obj.executionTime = jsonObj.executionTime;
			obj.users = jsonObj.users;
			obj.indicators = jsonObj.indicators;
			
			var groups = [];
			if(jsonObj.groups) {
				Ext.Array.forEach(jsonObj.groups, function(group) {
					var groupObj = {};
					groupObj.id = group.id;
					groupObj.descricao = group.descricao;
					
					var users = [];
					if(group.users) {
						Ext.Array.forEach(group.users, function(user) {
							var item = Ext.Array.findBy(obj.users, function(record) {
								return user.id === record.id;
							});
							
							if(item)
								users.push(item);
						});
					}
					groupObj.users = users;
					
					var indicators = [];
					if(group.indicators) {
						Ext.Array.forEach(group.indicators, function(indicator) {
							var item = Ext.Array.findBy(obj.indicators, function(record) {
								return indicator.id === record.id;
							});
							
							if(item)
								indicators.push(item);
						});
					}
					groupObj.indicators = indicators;
					
					groups.push(groupObj);
				});
			}
			
			obj.groups = groups;
		}

		this.callParent();
	},

	saveConfig: function(callback) {
		var data = {};

		data.server = this.server;
		data.logDirectory = this.logDirectory;
		
		var logProperties = [];
		Ext.Array.forEach(this.logProperties, function(property) {
			var rec = {};
			rec.infoPropriedade = property.infoPropriedade;
			rec.tipoValor = property.tipoValor;
			rec[data.server] = property[data.server];
			logProperties.push(rec);
		});		
		data.logProperties = logProperties;
		
		data.executionTime = this.executionTime;
		
		data.users = this.users;
		data.indicators = this.indicators;
		
		var groups = [];
		Ext.Array.forEach(this.groups, function(group) {
			 var rec = {};
			 rec.id = group.id;
			 rec.descricao = group.descricao;
			 
			 var users = [];
			 Ext.Array.forEach(group.users, function(user) {
				 users.push({
					 id: user.id
				 });
			 });			 
			 rec.users = users;
			 
			 var indicators = [];
			 Ext.Array.forEach(group.indicators, function(indicator) {
				 indicators.push({
					 id: indicator.id
				 });
			 });			 
			 rec.indicators = indicators;
			 
			 groups.push(rec);
		});
		data.groups = groups;
		
		fs.writeFile('./config/config.json', JSON.stringify(data), callback);		
	},

	run: function(callback) {
		this.saveConfig(callback);
	}
});