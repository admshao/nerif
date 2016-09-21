Ext.define('Nerif.component.usuario.Grid', {
    extend: 'Ext.grid.Panel',

    viewModel: {
        type: 'gerenciador'
    },

    //todo algum controle de id
    currentId: 0,

    bind: {
        store: '{users}'
    },

    columns: [{
        xtype: 'templatecolumn',
        text: 'Usuários',
        flex: 1,
        tpl: Ext.create('Ext.XTemplate',
            '<strong><span>{nome}<span></strong>',
            '<br />',
            '<span style="font-size: 12px">Email: {email}</span>',
            '<tpl if="telefone">',
            '   <br />',
            '   <span style="font-size: 12px">Telefone: {telefone}</span>',
            '</tpl>'
        )
    }, {
        xtype: 'actioncolumn',
        width: 50,
        items: [{
            icon: 'editar.png', //todo
            tooltip: 'Editar',
            handler: function (grid, rowIndex, colIndex) {
                var rec = grid.getStore().getAt(rowIndex);

                Ext.create('Nerif.component.usuario.Cadastro', {
                    listeners: {
                        'usuariosalvo': function (dados) {
                            rec.set(dados);
                        }
                    }
                }).editar(rec);
            }
        }, {
            icon: 'remover.png', //todo
            tooltip: 'Remover',
            handler: function (grid, rowIndex, colIndex) {
                Ext.Msg.confirm('ATENÇÃO', 'Esta ação irá remover este usuário de todos os grupos cadastrados. Deseja continuar?', function (btn) {
                    if (btn === 'yes') {
                        grid.getStore().removeAt(rowIndex);
                    }
                });
            }
        }]
    }],

    buttons: ['->', {
        text: 'Adicionar',
        bind: {
            disabled: '{!server}'
        },
        handler: function (me) {
            Ext.create('Nerif.component.usuario.Cadastro', {
                listeners: {
                    'usuariosalvo': function (dados) {
                        var grid = me.up('grid');

                        dados.id = grid.currentId++;
                        grid.getStore().add(dados);
                    }
                }
            }).show();
        }
    }]
});