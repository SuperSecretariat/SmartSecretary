export enum TicketType {
  BUG_REPORT = 'BUG_REPORT',
  INFORMATION_REQUEST = 'INFORMATION_REQUEST',
  FEATURE_REQUEST = 'FEATURE_REQUEST',
};

export const TicketTypeLabels: Record<TicketType, string> = {
  [TicketType.BUG_REPORT]: 'Bug Report',
  [TicketType.INFORMATION_REQUEST]: 'Information Request',
  [TicketType.FEATURE_REQUEST]: 'Feature Request',
};

export enum TicketStatus {
  OPEN = 'OPEN',
  CLOSED = 'CLOSED'
};

export const TicketStatusLabels: Record<TicketStatus, string> = {
  [TicketStatus.OPEN]: 'Open',
  [TicketStatus.CLOSED]: 'Closed',
}

export class TicketMessage {
  id: number;
  senderEmail: string;
  message: string;
  timestamp: Date;

  constructor(data: Partial<TicketMessage>) {
    this.id = data.id ?? -1;
    this.senderEmail = data.senderEmail ?? '';
    this.message = data.message ?? '';
    this.timestamp = data.timestamp ? new Date(data.timestamp) : new Date();
  }

  get formattedTimestamp(): string {
    return this.timestamp.toLocaleString();
  }
}

export class Ticket {
  id: number;
  subject: string;
  type: TicketType;
  status: TicketStatus;
  creatorEmail: string;

  constructor(data: Partial<Ticket>) {
    this.id = data.id ?? 0;
    this.subject = data.subject ?? '';
    this.type = (data.type as TicketType) ?? TicketType.BUG_REPORT;
    this.status = data.status === TicketStatus.CLOSED ? TicketStatus.CLOSED : TicketStatus.OPEN;
    this.creatorEmail = data.creatorEmail ?? '';
  }

  get isOpen(): boolean {
    return this.status === TicketStatus.OPEN;
  }

  toggleStatus(): void {
    this.status = this.isOpen ? TicketStatus.OPEN : TicketStatus.CLOSED;
  }
}
