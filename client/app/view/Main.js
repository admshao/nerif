Ext.define('Nerif.view.Main', {
    extend: 'Ext.tab.Panel',

    initComponent: function () {
        var obj = this;

        this.items = [{
            xclass: 'Nerif.view.tab.Geral'
        }];

        this.callParent();
    }
});