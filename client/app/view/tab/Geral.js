Ext.define('Nerif.view.tab.Geral', {
    extend: 'Ext.form.Panel',

    title: 'Geral',
    layout: 'border',
    bodyPadding: '10px',

    initComponent: function () {
        var obj = this;

        var dadosServidorContainer = Ext.create('Nerif.component.servidor.Dados', {
            region: 'north'
        });

        var usuariosGrid = Ext.create('Nerif.component.usuario.Grid', {
            flex: 1
        });

        var indicadoresGrid = Ext.create('Nerif.component.indicador.Grid', {
            flex: 2
        });

        var centerpanel = Ext.create('Ext.form.FieldContainer', {
            region: 'center',
            layout: {
                type: 'hbox',
                align: 'stretch'
            },
            defaults: {
                margin: '5px'
            },
            items: [usuariosGrid, indicadoresGrid]
        });

        var groupsGrid = Ext.create('Nerif.component.grupo.Grid', {
            region: 'south',
            flex: .7
        });

        var runBtn = Ext.create('Ext.button.Button', {
            formBind: true,
            text: 'Run',
            handler: function () {
                console.log(obj.getValues());
            }
        });

        this.items = [dadosServidorContainer, centerpanel, groupsGrid];
        this.buttons = ['->', runBtn];

        this.on('afterrender', function (me) {
            me.isValid();
        });

        this.callParent();
    }
});