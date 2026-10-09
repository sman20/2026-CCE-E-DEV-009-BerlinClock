import type { ButtonHTMLAttributes, ReactNode } from 'react';

type Props = ButtonHTMLAttributes<HTMLButtonElement> & {
  label: string;
  children: ReactNode;
  variant?: 'convert' | 'copy';
};

export default function IconButton({label, children, variant = 'copy', type = 'button', ...props}: Props) {
  return <button {...props} type={type} className={`icon-button ${variant}`}
    aria-label={label} title={label}>{children}</button>;
}
