export type PostType = 'ISSUE' | 'COMPLAINT' | 'SUGGESTION' | 'GENERAL' | 'OTHER';
export type PostStatus = 'DRAFT' | 'SUBMITTED' | 'UNDER_REVIEW' | 'RESOLVED' | 'CLOSED' | 'REJECTED';
export type Priority = 'LOW' | 'MEDIUM' | 'HIGH' | 'CRITICAL';

export interface Post {
  id: number;
  title: string;
  description: string;
  type: PostType;
  status: PostStatus;
  priority?: Priority | null;
  attachmentUrl?: string | null;
  authorId: number;
  authorUsername: string;
  assignedToId?: number | null;
  assignedToUsername?: string | null;
  createdAt: string;
  updatedAt: string;
  resolvedAt?: string | null;
}
