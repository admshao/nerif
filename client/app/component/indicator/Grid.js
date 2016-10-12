Ext.define('Nerif.component.indicator.Grid', {
	extend: 'Ext.grid.Panel',

	getNextId: function() {		
		return Ext.Array.max(Ext.Array.pluck(Gerenciador.indicators, 'id')) + 1 || 0;
	},

	store: Ext.create('Ext.data.Store', {
		fields: [{ name: 'id', type: 'int' }, 'descricao', 'regras'],
		data: Gerenciador.indicators,
		listeners: {
			'update': function(me) {
				Gerenciador.indicators = Ext.Array.pluck(me.getData().items, 'data');
			},
			'datachanged': function(me, value) {
				Gerenciador.indicators = Ext.Array.pluck(me.getData().items, 'data');
			}
		}
	}),

	columns: [{
		xtype: 'templatecolumn',
		text: 'Indicadores',
		flex: 1,
		tpl: '{descricao}'
	}, {
		xtype: 'actioncolumn',
		width: 60,
		items: [{
			iconCls: 'edit',
			tooltip: 'Editar',
			handler: function (grid, rowIndex, colIndex) {
				var rec = grid.getStore().getAt(rowIndex);

				Ext.create('Nerif.component.indicator.CadastroIndicador', {
					listeners: {
						'indicadorsalvo': function (dados) {
							rec.set(dados);
						}
					}
				}).editar(rec);
			}
		}, {
			iconCls: 'delete',
			tooltip: 'Remover',
			handler: function (grid, rowIndex, colIndex) {
				var rec = grid.getStore().getAt(rowIndex);

				for(var i = 0; i < Gerenciador.groups.length; i++) {
					var group = Gerenciador.groups[i];
					for(var j = 0; j < group.indicators.length; j++) {
						var indicator = group.indicators[j];
						if(indicator.id === rec.data.id) {
							Ext.Msg.alert('Erro', 'Este indicador está vinculado com um ou mais grupos.');
							return false;
						}
					}
				}

				Ext.Msg.confirm('Atenção', 'Está ação irá excluir este indicador. Deseja continuar?', function(btn) {
					if(btn === 'yes') {
						grid.getStore().remove(rec);		
					}
				});  
			}
		}]
	}],

	buttons: ['->', {
		text: 'Adicionar',
		handler: function (me) {
			Ext.create('Nerif.component.indicator.CadastroIndicador', {
				listeners: {
					'indicadorsalvo': function (dados) {
						var grid = me.up('grid');

						dados.id = grid.getNextId();
						grid.getStore().add(dados);
					}
				}
			}).show();
		}
	}]
});