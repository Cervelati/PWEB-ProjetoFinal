import { STATUS_LABEL } from '../format';
import { StatusPedido } from '../types';

export default function StatusBadge({ status }: { status: StatusPedido }) {
  return <span className={`badge ${status}`}>{STATUS_LABEL[status]}</span>;
}
