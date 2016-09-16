Ext.define('Nerif.view.Geral', {
	extend: 'Ext.form.Panel',

	title: 'Geral',

	layout: {
		type: 'vbox',
		align: 'stretch'
	},

	initComponent: function() {
		var obj = this;

		var servidorStore = Ext.create('Ext.data.Store', {
			fields: ['id', 'nome'],
			data: [
				{ id: 1, nome: 'Apache'},
				{ id: 2, nome: 'Nginx' },
				{ id: 3, nome: 'Windows Server' }
			]			
		});		

		var sevidorCombo = Ext.create('Ext.form.ComboBox', {
			fieldLabel: 'Servidor',
			valueField: 'id',
			displayField: 'nome',
			editable: false,
			store: servidorStore			
		});

		var diretorioLogText = Ext.create('Ext.form.Text', {
			fieldLabel: 'Diretório do log'
		});

		var formatoLogText = Ext.create('Ext.form.Text', {
			fieldLabel: 'Formato do log'
		});

		var dadosServidorFieldSet = Ext.create('Ext.form.FieldSet', {
			title: 'Dados do servidor',
			margin: '5px',
			fieldDefaults: {
				width: 500,
				labelAlign: 'right',
				allowBlank: false
			},
			items: [sevidorCombo, diretorioLogText, formatoLogText]
		});

		var usuariosStore = Ext.create('Ext.data.Store', {
			fields: ['id', 'nome', 'telefone', 'email']
		});

		var idUsuario = 0;

		var usuariosGrid = Ext.create('Ext.grid.Panel', {
			width: 300,
			height: 175,
			store: usuariosStore,
			columns: [{
				xtype: 'templatecolumn',
				text: 'Usuário',
				flex: 1,
				tpl: '{nome} - {email} - {telefone}'
			}],
			buttons: [{
				text: 'Adicionar',
				handler: function () {
					var id = ++idUsuario;

					usuariosStore.add({
						id: id,
						nome: 'Usuário ' + id,
						email: 'usuario_' + id + '@email.hue',
						telefone: '+555199887766'
					})
				}
			}]
		});

		var indicadoresStore = Ext.create('Ext.data.Store', {
			fields: ['id', 'descricao', 'infoPropriedade', 'tipoCategoria', 'tipoValor', 'valor1', 'valor2']
		});

		var indicadoresGrid = Ext.create('Ext.grid.Panel', {
			flex: 1,
			height: 175,
			store: indicadoresStore,
			columns: [{
				xtype: 'templatecolumn',
				text: 'Indicadores',
				flex: 1,
				tpl: '{id} {descricao} {infoPropriedade} {tipoCategoria} {tipoCategoria} {tipoValor} {valor1} {valor2}'
			}],
			buttons: [{
				text: 'Adicionar',
				handler: function () {

				}
			}]
		});

		var usuarioIndicadoresPanel = Ext.create('Ext.panel.Panel', {
			layout: {
				type: 'hbox'
			},
			defaults: {
				margin: '5px'
			},
			items: [usuariosGrid, indicadoresGrid]
		});

		var gruposStore = Ext.create('Ext.data.Store', {
			fields: ['id', 'descricao', 'usuarios', 'indicadores']
		});

		var idGrupo = 0;

		var gruposGrid = Ext.create('Ext.grid.Panel', {
			flex: 1,
			margin: '5px',
			store: gruposStore,
			columns: [{
				xtype: 'templatecolumn',
				text: 'Grupos',
				flex: 1,
				tpl: '{id} {descricao} {usuarios} {indicadores}'
			}],
			buttons: [{
				text: 'Adicionar',
				handler: function () {
					
				}
			}]
		});

		this.items = [dadosServidorFieldSet, usuarioIndicadoresPanel, gruposGrid];

		this.on('afterrender', function (me) {
			me.isValid();
		});

		this.callParent();
	}
});