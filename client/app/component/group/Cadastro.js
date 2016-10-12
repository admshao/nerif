Ext.define('Nerif.component.group.Cadastro', {
	extend: 'Ext.window.Window',

	modal: true,
	layout: 'fit',
	title: 'Cadastro de grupos',

	width: 600,
	height: 400,

	initComponent: function () {
		var obj = this;

		var grupoidHdn = Ext.create('Ext.form.Hidden', {
			name: 'id'
		});

		var descricaoText = Ext.create('Ext.form.Text', {
			region: 'north',
			allowBlank: false,
			name: 'descricao',
			fieldLabel: 'Descricao'
		});

		var usuariosGrid = Ext.create('Ext.grid.Panel', {
			flex: 1,
			store: Ext.create('Ext.data.Store', {
				fields: ['id', 'nome'],
				data: Gerenciador.users
			}),
			columns: [{
				flex: 1,
				text: 'Usuários',
				dataIndex: 'nome'
			}],
			selModel: {
				selType: 'checkboxmodel',
				checkOnly: true
			}
		});

		var indicadoresGrid = Ext.create('Ext.grid.Panel', {
			flex: 1,
			store: Ext.create('Ext.data.Store', {
				fields: ['id', 'descricao'],
				data: Gerenciador.indicators
			}),
			columns: [{
				flex: 1,
				text: 'Indicadores',
				dataIndex: 'descricao'
			}],
			selModel: {
				selType: 'checkboxmodel',
				checkOnly: true
			}
		});

		var usuariosIndicadoresPanel = Ext.create('Ext.form.FieldContainer', {
			region: 'center',
			padding: '5px 0',
			layout: {
				type: 'hbox',
				align: 'stretch'
			},
			defaults: {
				margin: '5px'
			},
			items: [usuariosGrid, indicadoresGrid]
		});

		var cancelarBtn = Ext.create('Ext.button.Button', {
			text: 'Cancelar',
			handler: function () {
				obj.close();
			}
		});

		var confirmarBtn = Ext.create('Ext.button.Button', {
			formBind: true,
			text: 'Salvar',
			handler: function () {
				var values = formpanel.getValues();

				values.users = Ext.Array.pluck(usuariosGrid.getSelectionModel().getSelection(), 'data');
				values.indicators = Ext.Array.pluck(indicadoresGrid.getSelectionModel().getSelection(), 'data');

				obj.fireEvent('gruposalvo', values);
				obj.close();
			}
		});

		var formpanel = Ext.create('Ext.form.Panel', {
			layout: 'border',
			bodyPadding: '10px',
			fieldDefaults: {
				labelAlign: 'right'
			},
			items: [grupoidHdn, descricaoText, usuariosIndicadoresPanel],
			buttons: ['->', cancelarBtn, confirmarBtn]
		});

		this.items = [formpanel];

		this.on('afterrender', function () {
			formpanel.isValid();
		});

		this.editar = function(rec) {
			obj.show();

			formpanel.loadRecord(rec);

			if(rec.data.users) {
				Ext.Array.forEach(rec.data.users, function(rec) {
					var idx = usuariosGrid.getStore().findExact('id', rec.id);
					if(idx !== -1)
						usuariosGrid.getSelectionModel().select(idx, true);
				});
			}

			if(rec.data.indicators) {
				Ext.Array.forEach(rec.data.indicators, function(rec) {
					var idx = indicadoresGrid.getStore().findExact('id', rec.id);
					if(idx !== -1)
						indicadoresGrid.getSelectionModel().select(idx, true);
				});
			}

			formpanel.isValid();
		};

		this.callParent();
	}
});