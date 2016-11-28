var fs = require('fs');
var path = require('path');
var dialog = require('electron').remote.dialog;

Ext.define('Nerif.view.tab.Teste', {
    extend: 'Ext.form.Panel',

    title: 'Testes',
    layout: 'border',
    bodyPadding: '10px',
    
    initComponent: function() {
    	var obj = this;
    	
    	var adicionarRegistro = function() {
    		
    		var linha = "";
    		
    		var fields = centerForm.query('textfield');
    		for(var i = 0; i < fields.length; i++) {
    			var f = fields[i];
    			
    			if(linha) linha += ' ';
    			linha += f.getValue();
    		}
    		
    		fs.appendFile(fileHdn.getValue(), '\r\n' + linha, function (err) {
    			if(err) {
    				Ext.Msg.alert('Atenção', 'Ocorreu um erro ao adicionar o registro');
    			} else {
    				Ext.Msg.alert('OK', 'Registro adiconado');
    			}
    		});
    		
    	};
    	
    	var iniciarTestes = function() {
    		
    		centerForm.removeAll(true);
    		
    		for(var i = 0; i < Gerenciador.logProperties.length; i++) {
    			var info = Gerenciador.logProperties[i];
    			
    			centerForm.add({
    				xtype: 'textfield',
    				allowBlank: false,
    				fieldLabel: info.description,    	
    				labelAlign: 'right',
    				labelWidth: 150
    			});
    			
    		}
    		
    		centerForm.add({
    			xtype: 'button',
    			formBind: true,
    			text: 'Adicionar registro',
    			handler: function() {
    				adicionarRegistro();
    			}
    		});
    		
    		centerForm.isValid();
    	};
    	
    	var iniciarArquivoTesteIIS = function() {
    		
    		var header = '';
    		header += '#Software: Microsoft Internet Information Services 7.5\r\n';
    		header += '#Version: 1.0\r\n';
    		header += '#Date: ' + Ext.Date.format(dataField.getValue(), 'Y-m-d 00:00:00') + '\r\n';
    		header += '#Fields: ' + Ext.Array.pluck(Gerenciador.logProperties, Gerenciador.server).join(' ');
    			
    		fs.writeFile(fileHdn.getValue(), header, function(err) {
    			if(err) {
    				Ext.Msg.alert('Atenção', 'Ocorreu um erro ao iniciar o teste');
    			} else {
    				iniciarTestes();
    			}
    		});    		
    	};
    	
    	var iniciarArquivoTeste = function() {
    		switch(Gerenciador.server) {
    		case 'iis':
    			iniciarArquivoTesteIIS();
    		}
    	};
    	
    	var fileHdn = Ext.create('Ext.form.Hidden');
    	
    	var dataField = Ext.create('Ext.form.Date', {
    		allowBlank: false,
    		fieldLabel: 'Data',
    		value: new Date(),
    		format: 'd/m/Y'
    	});
    	
    	var diretorioField = Ext.create('Ext.form.Text', {
			allowBlank: false,
			fieldLabel: 'Diretório',
			editable: false,
			width: 500,
			value: Gerenciador.logDirectory,
			triggers: {
				chooser: {
					cls: 'fa-folder-open',
					hideOnReadOnly: false,
					handler: function() {
						var dir = dialog.showOpenDialog({properties: ['openDirectory']});
						if(dir)
							logDirectoryText.setValue(dir);
					}
				}
			}
		}); 
    	
    	var iniciarTesteBtn = Ext.create('Ext.button.Button', {
    		formBind: true,
    		text: 'Criar arquivo de testes',
    		handler: function(me) {
    			var data = Ext.Date.format(dataField.getValue(), 'Y-m-d');
    			
    			fileHdn.setValue(path.join(diretorioField.getValue(), 'nerif_' + data + '.log'));
    			    			
    			if (fs.existsSync(fileHdn.getValue())) {
    				Ext.Msg.confirm('Atenção', 'Já existe um arquivo de testes para esta data. Todas as informações contidas nele serão perdidas. Deseja continuar?', function(btn) {
    					if(btn === 'yes') {
    						iniciarArquivoTeste();
    					}
    				});
    			} else {
    				iniciarArquivoTeste();
    			}    			
			}
    	}); 
    	
    	var configForm = Ext.create('Ext.form.Panel', {
    		region: 'north',
    		margin: '20px',
    		fieldDefaults: {
    			labelAlign: 'right'
    		},
    		items: [dataField, diretorioField, iniciarTesteBtn]
    	});
    	
    	var centerForm = Ext.create('Ext.form.Panel', {
    		region: 'north',    	
    		margin: '20px',
    		layout: 'form'
    	});
    	
    	this.items = [configForm, centerForm];
    	
    	this.callParent();
    }
});