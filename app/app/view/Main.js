Ext.define('Nerif.view.Main', {
	extend: 'Ext.tab.Panel',

	initComponent: function () {
		var obj = this;

		var dadosPanel = Ext.create('Nerif.view.Geral');

		this.items = [dadosPanel];

		this.callParent();
	}
});