var fs = require('fs');

Ext.define('Nerif.component.Gerenciador', {
	extend: 'Ext.Component',

	singleton: true,
	alternateClassName: ['Gerenciador'],

	server: '',
	logDirectory: '',
	logProperties: [],

	users: [],
	indicators: [],
	groups: [],

	getServerDescription: function() {
		if(!this.logProperties || this.logProperties.length === 0) return '';

		var html = '';

		html += 'Por favor, verifique se a configuração do formato do log do seu servidor começa da seguinte maneira: ';		
		html += '<br /><br />';				
		html += Ext.Array.pluck(this.logProperties, this.server).join(' ');

		return html;
	},

	initComponent: function() {
		var obj = this;

		if (fs.existsSync('config/config.json')) {
			var data = fs.readFileSync('config/config.json').toString();
			var jsonObj = Ext.decode(data);

			for(var key in jsonObj) {
				obj[key] = jsonObj[key];
			}
		}

		this.callParent();
	},

	saveConfig: function(callback) {
		var data = {};

		data.server = this.server;
		data.logDirectory = this.logDirectory;
		data.logProperties = this.logProperties;
		data.users = this.users;
		data.indicators = this.indicators;
		data.groups = this.groups;

		fs.writeFile('config/config.json', JSON.stringify(data), callback); 
	},

	run: function(callback) {
		this.saveConfig(callback);
	}
});