var exec = require('child_process').exec

Ext.define('Nerif.view.tab.Geral', {
    extend: 'Ext.form.Panel',

    title: 'Geral',
    layout: 'border',
    bodyPadding: '10px',

    initComponent: function () {
        var obj = this;

        var dadosServidorContainer = Ext.create('Nerif.component.server.Information', {
            region: 'north'
        });
        
        dadosServidorContainer.on('serverchanged', function(){
        	usuariosGrid.getStore().loadData(Gerenciador.users);
        	indicadoresGrid.getStore().loadData(Gerenciador.indicators);
        	groupsGrid.getStore().loadData(Gerenciador.groups);
        });
        
        var usuariosGrid = Ext.create('Nerif.component.user.Grid', {
            flex: 1
        });

        var indicadoresGrid = Ext.create('Nerif.component.indicator.Grid', {
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

        var groupsGrid = Ext.create('Nerif.component.group.Grid', {
            region: 'south',
            flex: .7
        });
        
        var saveBtn = Ext.create('Ext.button.Button', {
            text: 'Salvar configurações',
            handler: function () {
                Gerenciador.saveConfig(function(err) {
					if(err) {
						Ext.Msg.alert('Erro', 'Ocorreu um erro ao salvar suas configurações');
						console.error(err);
					} else {
						Ext.Msg.alert('Ok', 'Configurações salvas com sucesso');
					}
				});
            }
        });
        
        var runBtn = Ext.create('Ext.button.Button', {
            formBind: true,
            text: 'Start',
            handler: function () {
                /*Gerenciador.run(function(err) {
					if(err) {
						Ext.Msg.alert('Erro', 'Ocorreu um erro ao salvar suas configurações');
					} else {
						//todo ?
					}
				});*/
            	child = exec('java -jar ../bin/Nerif.jar -a -e',
    			function(error, stdout, stderr) {
					console.log('stdout: ' + stdout);
					console.log('stderr: ' + stderr);
					if (error !== null) {
						console.log('exec error: ' + error);
					}
			});
            }
        });

        this.items = [dadosServidorContainer, centerpanel, groupsGrid];
        this.buttons = ['->', saveBtn, runBtn];

        this.on('afterrender', function (me) {
            me.isValid();
        });

        this.callParent();
    }
});