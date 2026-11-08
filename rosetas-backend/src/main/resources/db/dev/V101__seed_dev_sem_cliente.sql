-- O seed V100 criou um usuário "cliente"; clientes não têm mais conta.
delete from usuarios where perfil = 'CLIENTE';
