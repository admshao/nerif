Ext.define('Nerif.view.Main', {
    extend: 'Ext.tab.Panel',

    initComponent: function () {
        var obj = this;

        this.items = [{
            xclass: 'Nerif.view.tab.Geral'
        }, {
        	xclass: 'Nerif.view.tab.Estatistica'
        },{
        	tabConfig : {
        		xtype : 'tbfill'
        	}
        }, {
        	xclass: 'Nerif.view.tab.Teste'
        }];

        this.callParent();
    }
});