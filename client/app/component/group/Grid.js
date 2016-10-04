Ext.define('Nerif.component.group.Grid', {
	extend: 'Ext.grid.Panel',

	getNextId: function() {
		return Ext.Array.max(Ext.Array.pluck(Gerenciador.groups, 'id')) + 1 || 0;
	},
	
	store: Ext.create('Ext.data.Store', {
		fields: ['id', 'users', 'indicators'],
		data: Gerenciador.groups,
		listeners: {
			'datachanged': function(me, value) {
				Gerenciador.groups = Ext.Array.pluck(me.getData().items, 'data');
			}
		}
	}),

	columns: [{
		xtype: 'templatecolumn',
		text: 'Grupos',
		flex: 1,
		tpl: Ext.create('Ext.XTemplate',
				'<strong><span>{descricao}</span></strong>',
				'<br/>',
				'<span>Usuários: {[this.formatData(values.users, "nome")]}</span>',
				'<br/>',
				'<span>Indicadores: {[this.formatData(values.indicators, "descricao")]}</span>',
				{
			formatData: function(data, property) {        			
				return Ext.Array.pluck(data, property).join(', ');
			}
				}
		)
	}, {
		xtype: 'actioncolumn',
		width: 60,
		items: [{
			icon: 'images/edit.png',
			tooltip: 'Editar',
			handler: function (grid, rowIndex, colIndex) {
				var rec = grid.getStore().getAt(rowIndex);

				Ext.create('Nerif.component.group.Cadastro', {
					listeners: {
						'gruposalvo': function (dados) {
							rec.set(dados);
						}
					}
				}).editar(rec);
			}
		}, {
			icon: 'images/delete.png',
			tooltip: 'Remover',
			handler: function (grid, rowIndex, colIndex) {
				Ext.Msg.confirm('Atenção', 'Está ação irá excluir este grupo. Deseja continuar?', function(btn) {
					if(btn === 'yes') {
						grid.getStore().removeAt(rowIndex);
					}
				});  
			}
		}]
	}],

	buttons: ['->', {
		text: 'Adicionar',
		handler: function (me) {
			Ext.create('Nerif.component.group.Cadastro', {
				listeners: {
					'gruposalvo': function(dados) {
						var grid = me.up('grid');

						dados.id = grid.getNextId();
						grid.getStore().add(dados);
					}
				}
			}).show();
		}
	}]
});